package infrastructure

import domain.model.*
import domain.service.CatalogoDeMagias
import domain.service.ConstrutorDeFicha
import infrastructure.persistence.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RepositorioIntegracaoTest {

    private val dbPath = "test_escudo.db"

    @BeforeEach
    fun setup() {
        File(dbPath).delete()
        SQLiteDatabaseFactory.reset()
        SQLiteDatabaseFactory.conectar(dbPath)
    }

    @Test
    fun `deve inserir e recuperar personagem`() {
        val dao = PersonagemDAO()
        val p = ConstrutorDeFicha.construir("Teste", Raca.HUMANO, Classe.GUERREIRO,
            mapOf("FOR" to 10, "DES" to 10, "CON" to 10, "INT" to 10, "SAB" to 10, "CAR" to 10))
        dao.inserir(p)
        val recuperado = dao.buscarPorId(p.id)
        assertNotNull(recuperado)
        assertEquals(p.nome, recuperado.nome)
        assertEquals(p.raca, recuperado.raca)
        assertEquals(p.classe, recuperado.classe)
    }

    @Test
    fun `deve atualizar personagem`() {
        val dao = PersonagemDAO()
        val p = ConstrutorDeFicha.construir("Teste", Raca.HUMANO, Classe.GUERREIRO,
            mapOf("FOR" to 10, "DES" to 10, "CON" to 10, "INT" to 10, "SAB" to 10, "CAR" to 10))
        dao.inserir(p)
        p.nome = "Teste Atualizado"
        p.pontosDeVidaAtual = 15
        dao.atualizar(p)
        val recuperado = dao.buscarPorId(p.id)
        assertEquals("Teste Atualizado", recuperado?.nome)
        assertEquals(15, recuperado?.pontosDeVidaAtual)
    }

    @Test
    fun `deve listar todos os personagens`() {
        val dao = PersonagemDAO()
        val p1 = ConstrutorDeFicha.construir("P1", Raca.HUMANO, Classe.GUERREIRO,
            mapOf("FOR" to 10, "DES" to 10, "CON" to 10, "INT" to 10, "SAB" to 10, "CAR" to 10))
        val p2 = ConstrutorDeFicha.construir("P2", Raca.ELFO, Classe.MAGO,
            mapOf("FOR" to 10, "DES" to 10, "CON" to 10, "INT" to 10, "SAB" to 10, "CAR" to 10))
        dao.inserir(p1)
        dao.inserir(p2)
        val lista = dao.listarTodos()
        assertEquals(2, lista.size)
    }

    @Test
    fun `deve deletar personagem`() {
        val dao = PersonagemDAO()
        val p = ConstrutorDeFicha.construir("Delete", Raca.HUMANO, Classe.GUERREIRO,
            mapOf("FOR" to 10, "DES" to 10, "CON" to 10, "INT" to 10, "SAB" to 10, "CAR" to 10))
        dao.inserir(p)
        dao.deletar(p.id)
        val recuperado = dao.buscarPorId(p.id)
        assertEquals(null, recuperado)
    }

    @Test
    fun `deve persistir item relacionado`() {
        val pDao = PersonagemDAO()
        val iDao = ItemDAO()
        val p = ConstrutorDeFicha.construir("Teste", Raca.HUMANO, Classe.GUERREIRO,
            mapOf("FOR" to 10, "DES" to 10, "CON" to 10, "INT" to 10, "SAB" to 10, "CAR" to 10))
        pDao.inserir(p)
        val item = Item("i1", "Poção", 0.5, 2)
        iDao.inserir(item, p.id)
        val itens = iDao.listarPorPersonagem(p.id)
        assertEquals(1, itens.size)
        assertEquals("Poção", itens[0].nome)
    }

    @Test
    fun `deve persistir magia relacionada`() {
        val pDao = PersonagemDAO()
        val mDao = MagiaDAO()
        val p = ConstrutorDeFicha.construir("Teste", Raca.HUMANO, Classe.MAGO,
            mapOf("FOR" to 10, "DES" to 10, "CON" to 10, "INT" to 10, "SAB" to 10, "CAR" to 10))
        pDao.inserir(p)
        val magia = CatalogoDeMagias.porNome("Bola de Fogo")!!.copiaParaGrimorio("m1")
        mDao.inserir(magia, p.id)
        val magias = mDao.listarPorPersonagem(p.id)
        assertEquals(1, magias.size)
        assertEquals("Bola de Fogo", magias[0].nome)
        assertEquals(EscolaDeMagia.EVOCACAO, magias[0].escola)
        assertEquals(3, magias[0].nivel)
        assertEquals(true, magias[0].preparada)
    }

    @Test
    fun `deve persistir historico`() {
        val hDao = HistoricoDAO()
        val h = Historico("h1", "p1", "ATAQUE", 15, System.currentTimeMillis())
        hDao.inserir(h)
        val lista = hDao.listarPorPersonagem("p1")
        assertTrue(lista.isNotEmpty())
        assertEquals("ATAQUE", lista[0].tipoRolagem)
    }

    @Test
    fun `deve listar historico em ordem decrescente`() {
        val hDao = HistoricoDAO()
        val t1 = System.currentTimeMillis()
        val t2 = t1 + 1000
        hDao.inserir(Historico("h1", "p1", "ATAQUE", 10, t1))
        hDao.inserir(Historico("h2", "p1", "DANO", 20, t2))
        val lista = hDao.listarPorPersonagem("p1")
        assertEquals(2, lista.size)
        assertEquals("DANO", lista[0].tipoRolagem) // mais recente primeiro
    }

    @Test
    fun `deve persistir magia completa com todos os campos`() {
        val pDao = PersonagemDAO()
        val mDao = MagiaDAO()
        val p = ConstrutorDeFicha.construir("Teste", Raca.HUMANO, Classe.MAGO,
            mapOf("FOR" to 10, "DES" to 10, "CON" to 10, "INT" to 10, "SAB" to 10, "CAR" to 10))
        pDao.inserir(p)
        val magia = CatalogoDeMagias.porNome("Bola de Fogo")!!.copiaParaGrimorio("mf1")
        mDao.inserir(magia, p.id)
        val recuperada = mDao.buscarPorId("mf1")!!
        assertEquals("Bola de Fogo", recuperada.nome)
        assertEquals(EscolaDeMagia.EVOCACAO, recuperada.escola)
        assertEquals("1 ação", recuperada.tempoConjuracao)
        assertEquals(3, recuperada.nivel)
        assertTrue(recuperada.preparada)
        assertFalse(recuperada.slotGasto)
        assertTrue(recuperada.descricao.isNotBlank())
    }

    @Test
    fun `deve persistir equipamento e escudo`() {
        val dao = PersonagemDAO()
        val p = ConstrutorDeFicha.construir(
            "Guerreira", Raca.HUMANO, Classe.GUERREIRO,
            mapOf("FOR" to 15, "DES" to 12, "CON" to 13, "INT" to 10, "SAB" to 10, "CAR" to 8),
            arma = Arma.ESPADA_LARGA,
            armadura = Armadura.PLACAS,
            escudo = true,
        )
        dao.inserir(p)
        val recuperado = dao.buscarPorId(p.id)!!
        assertEquals(Arma.ESPADA_LARGA, recuperado.armaEquipada)
        assertEquals(Armadura.PLACAS, recuperado.armaduraEquipada)
        assertTrue(recuperado.escudoEquipado)
        assertEquals(18 + 2, recuperado.classeArmadura) // placas + escudo
    }

    @Test
    fun `deve atualizar status de magia no grimorio`() {
        val pDao = PersonagemDAO()
        val mDao = MagiaDAO()
        val p = ConstrutorDeFicha.construir("Teste", Raca.HUMANO, Classe.MAGO,
            mapOf("FOR" to 10, "DES" to 10, "CON" to 10, "INT" to 16, "SAB" to 10, "CAR" to 10))
        pDao.inserir(p)
        val magia = CatalogoDeMagias.porNome("Bola de Fogo")!!.copiaParaGrimorio("mst1")
        mDao.inserir(magia, p.id)
        mDao.atualizarStatus("mst1", preparada = true, slotGasto = true)
        val recuperada = mDao.buscarPorId("mst1")!!
        assertTrue(recuperada.preparada)
        assertTrue(recuperada.slotGasto)
    }

    @Test
    fun `deve deletar magia e item relacionados`() {
        val pDao = PersonagemDAO()
        val mDao = MagiaDAO()
        val iDao = ItemDAO()
        val p = ConstrutorDeFicha.construir("Teste", Raca.HUMANO, Classe.MAGO,
            mapOf("FOR" to 10, "DES" to 10, "CON" to 10, "INT" to 10, "SAB" to 10, "CAR" to 10))
        pDao.inserir(p)
        val magia = CatalogoDeMagias.porNome("Escudo")!!.copiaParaGrimorio("md1")
        mDao.inserir(magia, p.id)
        iDao.inserir(Item("di1", "Poção", 0.5, 1), p.id)
        mDao.deletar("md1")
        iDao.deletar("di1")
        assertEquals(0, mDao.listarPorPersonagem(p.id).size)
        assertEquals(0, iDao.listarPorPersonagem(p.id).size)
    }

    @Test
    fun `deve persistir estado de combate alterado manualmente`() {
        val dao = PersonagemDAO()
        val p = ConstrutorDeFicha.construir("Estado", Raca.HUMANO, Classe.GUERREIRO,
            mapOf("FOR" to 10, "DES" to 10, "CON" to 10, "INT" to 10, "SAB" to 10, "CAR" to 10))
        p.estadoCombate = EstadoCombate.ATURDIDO_INCAPACITADO
        dao.inserir(p)
        val recuperado = dao.buscarPorId(p.id)!!
        assertEquals(EstadoCombate.ATURDIDO_INCAPACITADO, recuperado.estadoCombate)
    }
}
