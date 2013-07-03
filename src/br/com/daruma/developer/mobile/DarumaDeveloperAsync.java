package br.com.daruma.developer.mobile;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.util.Log;
import br.com.daruma.framework.mobile.exception.DarumaException;

/**
 * Tipo de Dados possiveis no input das funções a serem validadas
 * @author Rafael
 *
 */
enum TipoDadoDaruma
{
    Numerico, ValorUn, Porcentagem, Quantidade, Alfanumerico, AlfanumericoVariavel
};


/**
 * Abstrai os comandos utilizados nas Impressoras Daruma em funções de alto nível. * 
 * @author Rafael
 *
 */
public class DarumaDeveloperAsync implements OnDarumaMobileListener {
	
	/**
	 * Enum utilizado para determinar qual metodo está sendo executado async.
	 */
	enum MetodoExecutando {
		inicializa,
		iniciarComunicacaoAsync, confParametrosAsync, fecharComunicacaoAsync,
		rLerDecimaisQuantidade_ECF_DarumaAsync, rLerDecimaisValorUnitario_ECF_DarumaAsync, rLerAliquotas_ECF_DarumaAsync, rLerMeiosPagto_ECF_DarumaAsync, rLerCNF_ECF_DarumaAsync, rLerRG_ECF_DarumaAsync, rRetornarInformacao_ECF_DarumaAsync, 
		iCFAbrir_ECF_DarumaAsync, iCFVender_ECF_DarumaAsync, iCFTotalizarCupom_ECF_DarumaAsync, iCFEfetuarPagamento_ECF_DarumaAsync, iCFEncerrar_ECF_DarumaAsync, iImprimirTexto_DUAL_DarumaAsync,  
		
	}

	private static final String TAG = "DarumaDeveloperAsync";
		
	private OnResponseDeveloperListener listener;
	
	private DarumaMobileAsync darumaMobile;

	private ArrayDeque<MetodoExecutando> filaMetodos = new ArrayDeque<MetodoExecutando>();

    //Java não suporta char especiais como % ou $ como identificador em um enum
    private ArrayList<String> tiposAcrescimosDecrescimos = new ArrayList<String>();

	private String strConexao = "";

    private ArrayList<String> aliquotas = new ArrayList<String>();
    public ArrayList<String> getListaAliquotas () {
        return aliquotas; 
    }
    private ArrayList<String> aliquotasAux = new ArrayList<String>();
    public ArrayList<String> getListaAliquotasAux()  {
        return aliquotasAux; 
    }

    private ArrayList<String> meiosPagto = new ArrayList<String>();
    public ArrayList<String> getListaMeiosPagto()  {
        return meiosPagto; 
    }
    private ArrayList<String> naoFiscais = new ArrayList<String>();
    public ArrayList<String> getListaNaoFiscais()  {
        return naoFiscais; 
    }
    private ArrayList<String> relatoriosGerais = new ArrayList<String>();
    public ArrayList<String> getListaRelatoriosGerais()  {
        return relatoriosGerais; 
    }


	private HashMap<Integer, String> erros = new HashMap<Integer, String>();
	public HashMap<Integer, String> getErros()  {
        return erros;
    }
	
    private HashMap<Integer, String> avisos = new HashMap<Integer, String> ();
    public HashMap<Integer, String> getAvisos()  {
        return avisos;
    }


    private int decimaisValorUn = 2;
    public int getDecimaisValorUn() {
        return decimaisValorUn; 
    }

    private int decimaisQuantidade = 2;
    public int getDecimaisQuantidade() {
        return decimaisQuantidade; 
    }


    private int codigoUltimoErro = 1;
    public int CodigoUltimoErro() {
        return codigoUltimoErro; 
    }

    private int codigoUltimoAviso = 1;
    public int CodigoUltimoAviso()  {
        return codigoUltimoAviso; 
    }



    private String iCOOCupomAberto = "";
    public String COOCupomAberto()  {
        return iCOOCupomAberto;
    }

    private String iCCFCupomAberto = "";
    public String CCFCupomAberto() {
        return iCCFCupomAberto;
    }


    private String iNItemVendido = "";
    public String getNItemVendido()  {
        return iNItemVendido;
    }

    private String iTipoDescontoItemVendido = "";
    public String getTipoDescontoItemVendido() {
        return iTipoDescontoItemVendido; 
    }

    private String iTotalLiquidoItemVendido = "";
    public String getTotalLiquidoItemVendido() {
        return iTotalLiquidoItemVendido;
    }


    private String iSubTotalCupomTotalizado = "";
    public String getSubTotalCupomTotalizado() {
        return iSubTotalCupomTotalizado;
    }


    private String iSaldoOuTrocoPagamentoEfetuado = "";
    public String SaldoOuTrocoPagamentoEfetuado() {
        return iSaldoOuTrocoPagamentoEfetuado;
    }


    private String iCCOCupomEncerrado = "";
    public String getCOOCupomEncerrado() {
        return iCCOCupomEncerrado;
    }

    private String iTotalLiquidoCupomEncerrado = "";
    public String getTotalLiquidoCupomEncerrado() {
        return iTotalLiquidoCupomEncerrado;
    }

    private String iSerieMFDE = "";
    public String getSerieMFDE() {
        return iSerieMFDE;
    }
	
	/**
	 * Contrutor utilizando um objeto DarumaMobile já inicializado.
	 * 
	 * @param dmf DarumaMobileFramework já inicializado!
	 * @param listener
	 * @throws DarumaException
	 */
	public DarumaDeveloperAsync(DarumaMobileAsync dmf, OnResponseDeveloperListener listener) throws DarumaException{
		this.darumaMobile = dmf;
		init();
	}
	
    /**
     * Construtor que também inicia um objeto DarumaMobile próprio e privado.
     * @param host IP ou Nome na Rede da impressora.
     * @param port Port na qual a conexão será efetuada.
     * @param timeout Timeout utilizado para a conexão.
     * @param Se verdadeiro, utiliza operações atomicas, ou seja, à cada operação será iniciada uma nova conexão.
     */
	public DarumaDeveloperAsync(String host, int port, int timeout, boolean fechaconexao, OnResponseDeveloperListener listener){
        strConexao =   "@FRAMEWORK(TRATAEXCECAO=TRUE);@SOCKET(HOST="+host+";PORT="+port+";TIMEOUT="+timeout+")";
        
        this.listener = listener;
        darumaMobile = DarumaMobileAsync.inicializarAsync(strConexao, this);
        init();
    }

	public void init(){

        //Aliquotas Auxiliares pre cadastradas
        aliquotasAux.add("F1");
        aliquotasAux.add("F2");
        aliquotasAux.add("I1");
        aliquotasAux.add("I2");
        aliquotasAux.add("N1");
        aliquotasAux.add("N2");
        aliquotasAux.add("FS1");
        aliquotasAux.add("FS2");
        aliquotasAux.add("IS1");
        aliquotasAux.add("IS2");
        aliquotasAux.add("NS1");
        aliquotasAux.add("NS2");
		
        //Java não suporta char especiais como % ou $ como identificador em um enum
        tiposAcrescimosDecrescimos.add("D%");
        tiposAcrescimosDecrescimos.add("D$");
        tiposAcrescimosDecrescimos.add("A%");
        tiposAcrescimosDecrescimos.add("A$");

        //Erros necessários ao framework
        erros.put(-3, "Tamanho do parametro superou o máximo permitido");
        erros.put(-2, "Elemento não cadastrado");
        erros.put(-1, "Erro não identificado");
        
        //Erros da Tabela de Erros
        erros.put(  1, "ECF com falha mecânica");
        erros.put(  2, "MF não conectada");
        erros.put(  3, "MFD não conectada");
        erros.put(  4, "MFD esgotada");
        erros.put(  5, "Erro na comunicação com a MF");
        erros.put(  6, "Erro na comunicação com a MFD");
        erros.put(  7, "MF não inicializada");
        erros.put(  8, "MFD não inicializada");
        erros.put(  9, "MFD já inicializada");
        erros.put( 10, "MFD foi substituída");
        erros.put( 11, "MFD já cadastrada");
        erros.put( 12, "Erro na inicialização da MFD");
        erros.put( 13, "Faltam parâmetros de inicialização na MF");
        erros.put( 14, "Comando não suportado");
        erros.put( 15, "Superaquecimento da cabeça de impressão");
        erros.put( 16, "Perda de dados da MT");
        erros.put( 17, "Operação habilitada apenas em MIT");
        erros.put( 18, "Operação habilitada apenas em modo fiscal");
        erros.put( 19, "Data inexistente");
        erros.put( 20, "Data inferior ao do último documento");
        erros.put( 21, "Intervalo inconsistente");
        erros.put( 22, "Não existem dados");
        erros.put( 23, "Clichê de formato inválido");
        erros.put( 24, "Erro no verificador da comunicação");
        erros.put( 25, "Senha incorreta");
        erros.put( 26, "Número de decimais para quantidade inválido");
        erros.put( 27, "Número de decimais para valor unitário inválido");
        erros.put( 28, "Tipo de impressão de FD inválido");
        erros.put( 29, "Caracter não estampável");
        erros.put( 30, "Caracter não estampável ou em branco");
        erros.put( 31, "Caracteres não podem ser repetidos");
        erros.put( 32, "Limite de itens atingido");
        erros.put( 33, "Todos os totalizadores fiscais já estão programados");
        erros.put( 34, "Totalizador fiscal já programado");
        erros.put( 35, "Todos os totalizadores não fiscais já estão programados");
        erros.put( 36, "Totalizador não fiscal já programado");
        erros.put( 37, "Todos os relatórios gerenciais já estão programados");
        erros.put( 38, "Relatório gerencial já programado");
        erros.put( 39, "Meio de pagamento já programado");
        erros.put( 40, "Índice inválido");
        erros.put( 41, "Índice do meio de pagamento inválido");
        erros.put( 42, "Erro gravando número de decimais na MF");
        erros.put( 43, "Erro gravando moeda na MF");
        erros.put( 44, "Erro gravando símbolos de decodificação do GT na MF");
        erros.put( 45, "Erro gravando número de fabricação da MFD na MF");
        erros.put( 46, "Erro gravando usuário na MF");
        erros.put( 47, "Erro gravando GT do usuário anterior na MF");
        erros.put( 48, "Erro gravando registro de marcação na MF");
        erros.put( 49, "Erro gravando CRO na MF");
        erros.put( 50, "Erro gravando impressão de FD na MF");
        erros.put( 51, "Campo em branco ou zero não permitido");
        erros.put( 52, "Campo reservado a gravação da moeda na MF esgotado");
        erros.put( 53, "Campo reservado a gravação da tabela de GT na MF esgotado");
        erros.put( 54, "Campo reservado a gravação do NS da MFD na MF esgotado");
        erros.put( 55, "Campo reservado a gravação de usuário na MF esgotado");
        erros.put( 56, "CNPJ inválido");
        erros.put( 57, "CRZ e CRO em zero");
        erros.put( 58, "Intervalo invertido");
        erros.put( 59, "Utilize apenas 0 ou 1");
        erros.put( 60, "Configuração permitida apenas imediatamente a RZ");
        erros.put( 61, "Símbolo gráfico inválido");
        erros.put( 62, "Falta pelo menos 1 campo no nome da moeda para cheque");
        erros.put( 63, "Código supera o valor 255");
        erros.put( 64, "Utilize valores entre 25 e 80");
        erros.put( 65, "Utilize valores entre 1 e 15");
        erros.put( 66, "Utilize valores entre 0 e 7250");
        erros.put( 67, "Data informada não coincide com a data do ECF");
        erros.put( 68, "Deve ajustar o relógio ( utilize o comando [FS] M <200> )");
        erros.put( 69, "Erro ao ajustar o relógio");
        erros.put( 70, "Capacidade da MF esgotada");
        erros.put( 71, "Versão do SB gravado na MF incorreta");
        erros.put( 72, "Fim do papel");
        erros.put( 73, "Nenhum usuário programado");
        erros.put( 74, "Utilize apenas dígitos numéricos");
        erros.put( 75, "Campo não pode estar em zero");
        erros.put( 76, "Campo não pode estar em branco");
        erros.put( 77, "Valor da operação não pode ser zero");
        erros.put( 78, "CF aberto");
        erros.put( 79, "CNF aberto");
        erros.put( 80, "CCD aberto");
        erros.put( 81, "RG aberto");
        erros.put( 82, "CF não aberto");
        erros.put( 83, "CNF não aberto");
        erros.put( 84, "CCD não aberto");
        erros.put( 85, "RG não aberto");
        erros.put( 86, "CCD ou RG não aberto");
        erros.put( 87, "Documento já totalizado");
        erros.put( 88, "RZ do movimento anterior pendente");
        erros.put( 89, "Já emitiu RZ de hoje");
        erros.put( 90, "Totalizador sem alíquota programada");
        erros.put( 91, "Campo de código ausente");
        erros.put( 92, "Campo de descrição ausente");
        erros.put( 93, "VU ou quantidade em zero");
        erros.put( 94, "Item ainda não vendido");
        erros.put( 95, "Desconto ou acréscimo não pode ser zero");
        erros.put( 96, "Item já possui desconto ou acréscimo");
        erros.put( 97, "Ítem cancelado");
        erros.put( 98, "Operação inibida por configuração");
        erros.put( 99, "Opção não suportada");
        erros.put(100, "Desconto ou acréscimo supera valor bruto");
        erros.put(101, "Desconto ou acréscimo final de valor zero");
        erros.put(102, "Valor bruto zero");
        erros.put(103, "Overflow no valor do item");
        erros.put(104, "Overflou no valor do desconto ou acréscimo");
        erros.put(105, "Overflow na capacidade do documento");
        erros.put(106, "Overflow na capacidade do totalizador");
        erros.put(107, "Item não possui desconto");
        erros.put(108, "Item já possui desconto");
        erros.put(109, "Quantidade possui mais de 2 decimais");
        erros.put(110, "Valor unitário possui mais de 2 decimais");
        erros.put(111, "Quantidade a cancelar deve ser inferior a total");
        erros.put(112, "Campo de descrição deste item não mais presente na MT");
        erros.put(113, "Subtotal não possui desconto ou acréscimo");
        erros.put(114, "Não em fase de totalização");
        erros.put(115, "Não em fase de venda ou totalização");
        erros.put(116, "Mais de 1 desconto ou acréscimo não permitido");
        erros.put(117, "Valor do desconto ou acréscimo supera subtotal");
        erros.put(118, "Meio de pagamento não programado");
        erros.put(119, "Não em fase de pagamento ou totalização");
        erros.put(120, "Não em fase de finalização de documento");
        erros.put(121, "Já emitiu mais CCDs que poderia estornar");
        erros.put(122, "Último documento não é cancelável");
        erros.put(123, "Estorne CCDs");
        erros.put(124, "Último documento não foi CF");
        erros.put(125, "Último documento não foi CNF");
        erros.put(126, "Não pode cancelar");
        erros.put(127, "Pagamento não mais na MT");
        erros.put(128, "Já emitiu CCD deste pagamento");
        erros.put(129, "RG não programado");
        erros.put(130, "CNF não programado");
        erros.put(131, "Cópia não disponível");
        erros.put(132, "Já emitiu segunda via");
        erros.put(133, "Já emitiu reimpressão");
        erros.put(134, "Informações sobre o pagamento não disponíveis");
        erros.put(135, "Já emitiu todas as parcelas");
        erros.put(136, "Parcelamento somente na sequência");
        erros.put(137, "CCD não encontrado");
        erros.put(138, "Não pode utilizar SANGRIA ou SUPRIMENTO");
        erros.put(139, "Pagamento não admite CCD");
        erros.put(140, "Relógio inoperante");
        erros.put(141, "Usuário sem CNPJ");
        erros.put(142, "Usuário sem IM");
        erros.put(143, "Não se passou 1 hora após o fechamento do último documento");
        erros.put(144, "ECF OFF LINE");
        erros.put(145, "Documento em emissão");
        erros.put(146, "COO não coincide");
        erros.put(147, "Erro na autenticação");
        erros.put(148, "Erro na impressão de cheque");
        erros.put(149, "Data não pertence ao século XXI");
        erros.put(150, "Usuário já programado");
        erros.put(151, "Descrição do pagamento já utilizada");
        erros.put(152, "Descrição do totalizador já utilizada");
        erros.put(153, "Descrição do RG já utilizada");
        erros.put(154, "Já tem desconto após acréscimo ( ou vice versa )");
        erros.put(155, "Já programou 15 totalizadores para ICMS");
        erros.put(156, "Já programou 15 totalizadores para ISS");
        erros.put(157, "MFD com problemas");
        erros.put(158, "Razão social excede 48 caracteres");
        erros.put(159, "Nome fantasia excede 48 caracteres");
        erros.put(160, "Endereço excede 120 caracteres");
        erros.put(161, "Identificação do programa aplicativo ausente");
        erros.put(162, "Valor de desconto supera valor acumulado em totalizador");
        erros.put(163, "Número de parcelas no pagamento não pode exceder 24");
        erros.put(164, "MFD não cadastrada");
        erros.put(165, "Excedeu limite de impressão de FD ( capacidade na MF esgotada )");
        erros.put(166, "Efetivado é igual ao estornado");
        erros.put(167, "Símbolo da moeda já programado");
        erros.put(168, "UF inválida");
        erros.put(169, "UF já programada");
        erros.put(170, "Erro gravando UF");
        erros.put(171, "Leitor CMC-7 não instalado");
        erros.put(172, "Erro de leitura do código CMC-7");
        erros.put(173, "Autenticação não permitida");
        erros.put(174, "Operação somente com mecanismo matricial de impacto");
        erros.put(175, "Coordenadas de cheque inválidas");
        erros.put(176, "Impressão do verso do cheque somente após a impressão da frente");
        erros.put(177, "Indice do bitmap inválido");
        erros.put(178, "Bitmap de tamanho inválido");
        erros.put(179, "Última RZ a mais de 30 dias. Comando de RZ deve informar data correta");
        erros.put(184, "Parâmetro só pode ser “A” ou “T”");
        erros.put(185, "Falta unidade doproduto");
        erros.put(186, "Velocidade não permitida");
        erros.put(187, "Código repetido");
        erros.put(188, "Fora dos limites");
        erros.put(189, "Já identificou o consumidor");
        erros.put(190, "Número de Fabricação incorreto");
        erros.put(191, "Informação disponível não corresponde a MF informada");
        erros.put(192, "MF já em uso");
        erros.put(193, "Falha não recuperável durante a operação");
        erros.put(194, "Opção inválida");
        erros.put(195, "Parâmetros inválidos");
        erros.put(196, "Caracter HEXA inválido");
        erros.put(197, "Valor insuficiente de pagamento");
        erros.put(198, "IE inválido");
        erros.put(199, "IM inválido");
        erros.put(301, "CFBP Inibido");
        erros.put(302, "Modalidade de Transporte inválida");
        erros.put(303, "Categoria de Transporte inválida");
        erros.put(304, "UF incompatível ");
        erros.put(305, "Comando disponível apenas em CF genérico");
        erros.put(400, "Chave não carregada");
        erros.put(401, "Chave inválida");
        erros.put(402, "Erro na decodificaçào");
        erros.put(403, "Erro na codificação");
        
        //Erros da Tabela de Avisos
        avisos.put(0, "Sem Aviso.");
        avisos.put(1, "Papel acabando");
        avisos.put(2, "Tampa aberta.");
        avisos.put(4, "Bateria fraca.");
        avisos.put(40, "Compactando.");
	}

	/**
	 * Conecta à impressora e retorna informações essenciais para o funcionamento das impressoras fiscais. Se utilizar DUAL, não inicialize, somente conecte.
	 */
    public void inicializa()
    {
        iniciarComunicacaoAsync();

        DMF_UTIL_PreencheTabelas();

        rLerDecimaisQuantidade_ECF_DarumaAsync();
        rLerDecimaisValorUnitario_ECF_DarumaAsync();
        
        //Este calback deve executar depois que todos os outros já executaram. O Padrão é que eles sejam executados na ordem em que foram chamados...
		filaMetodos.add(MetodoExecutando.inicializa);
		
        fecharComunicacaoAsync(); 	
    }
    
    //Exposição de alguns metodos da camada base
    
    /**
     * Inicializa a conexão utilizando os parametros utilizados no objeto DarumaMobile já existe, ou passados no construtor.
     */
    public void iniciarComunicacaoAsync() throws DarumaException
    {
		filaMetodos.add(MetodoExecutando.iniciarComunicacaoAsync);				
		darumaMobile.iniciarComunicacaoAsync();		
    }

    /**
     * Configura os Parametros da Conexao para reconectar à Impressora.
     * @param host IP ou Nome na Rede da impressora.
     * @param port Port na qual a conexão será efetuada.
     * @param timeout Timeout utilizado para a conexão.
     * @param trataexcecao Se veridadeiro, lança exceções para o usuário tratar.
     * @param fechaconexao Se verdadeiro, utiliza operações atomicas, ou seja, à cada operação será iniciada uma nova conexão.
     */
    public void confParametrosAsync(String host, int port, int timeout, boolean fechaconexao, OnResponseDeveloperListener listener ) throws DarumaException
    {
		strConexao = "@FRAMEWORK(TRATAEXCECAO=TRUE);@SOCKET(HOST="+host+";PORT="+port+";TIMEOUT="+timeout+")";
		this.listener = listener;
		
		filaMetodos.add(MetodoExecutando.confParametrosAsync);		
		darumaMobile.confParametrosAsync(strConexao);
    	
    }

    /**
     * Encerra a conexão atual com a Impressora.
     */
    public void fecharComunicacaoAsync() throws DarumaException
    {
		filaMetodos.add(MetodoExecutando.fecharComunicacaoAsync);		
		darumaMobile.fecharComunicacaoAsync();      
    }

    public void finalize(){
		darumaMobile.finalizeAsync();      	
    }
    
	//GRUPO CUPOM FISCAL

    /**
     * Este método abre um Cupom Fiscal, identificando consumidor.
     * @param CPF CPF ou CNPJ Consumidor. 20 caracteres com máscara. Alfanumérico.
     * @param Nome Nome Consumidor. 30 caracteres com máscara. Alfanumérico.
     * @param Endereco Endereço Consumidor. 79 caracteres com máscara. Alfanumérico.
     */
    public void iCFAbrir_ECF_DarumaAsync(String CPF, String Nome, String Endereco)
    {
        int cod = 200;

        CPF = DMF_UTIL_Valida(CPF, 20, TipoDadoDaruma.AlfanumericoVariavel);
        Nome = DMF_UTIL_Valida(Nome, 30, TipoDadoDaruma.AlfanumericoVariavel);
        Endereco = DMF_UTIL_Valida(Endereco, 79, TipoDadoDaruma.AlfanumericoVariavel);

        String param = CPF + Nome + Endereco;

		filaMetodos.add(MetodoExecutando.iCFAbrir_ECF_DarumaAsync);
        darumaMobile.enviarComando_FS_F_Async( String.valueOf(cod) + param);        
    }

    /**
     * Este método abre um Cupom Fiscal. 
     */
    public void iCFAbrirPadrao_ECF_DarumaAsync()
    {
        iCFAbrir_ECF_DarumaAsync("", "", "");
    }

    /**
     * Este método vende um item no Cupom Fiscal. 
     * @param CargaTributaria Alíquota do Item. A alíquota pode ser informada com a virgula (I07,00) ou sem a virgula (I0700).  
     * Exemplos: 
     * ICMS Não tributado: ("II" - Isento, "FF"- Substituição  tributária, "NN" - Não Tributária). 
     * ICMS Tributado:( I07,00 , I18,00,  I0700 , I1800) 
     * ISSQN Não tributado: ("ISS" - Isento, "FS"- Substituição tributária, "NS" - Não Tributária). 
     * ISSQN Tributado: (S07,00 , S18,00, S0700 , S1800) 
     * 5 caracteres. Alfanumérico.
     * @param Quantidade Quantidade do Item. 7 caracteres. Real.
     * @param PrecoUnitario Preço Unitário do Item. 7 caracteres. Real.
     * @param TipoDescAcresc Tipo Acréscimo ou Desconto -  Exemplo -
     *  D% - Desconto em Percentual 
     *  D$ - Desconto em Valor
     *  A% - Acréscimo em Percentual
     *  A$ - Acréscimo em Valor 
     *  2 caracteres. Alfanumérico.
     * @param ValorDescAcresc Valor do acréscimo ou Valor da porcentagem. 11 caracteres. Real.
     * @param TruncarOuArredondar Indicador de modo de cálculo
     * “T” para truncamento 
     * “A” para arredondamento
     * 1 caracteres. Alfanumérico.
     * @param CodigoItem Código do Item. 14 caracteres. Alfanumérico.
     * @param UnidadeMedida Unidade de medida.  3 caracteres. Alfanumérico.
     * @param DescricaoItem Descrição do Item.  233 caracteres. Alfanumérico.
     * @param TamMinDescItem Tamanho mínimo da descrição, no caso de impressão em 1 única linha. Se zero, não tenta imprimir em uma única linha.  2 caracteres. Numérico.
     */
    public void iCFVender_ECF_DarumaAsync(String CargaTributaria, String Quantidade, String PrecoUnitario, String TipoDescAcresc, String ValorDescAcresc, String TruncarOuArredondar, String CodigoItem, String UnidadeMedida, String DescricaoItem, String TamMinDescItem)
    {
        int cod = 207;

        DescricaoItem = DMF_UTIL_Valida(DescricaoItem, 233, TipoDadoDaruma.AlfanumericoVariavel);
        Quantidade = DMF_UTIL_Valida(Quantidade, 7, TipoDadoDaruma.Quantidade);
        PrecoUnitario = DMF_UTIL_Valida(PrecoUnitario, 8, TipoDadoDaruma.ValorUn);
        UnidadeMedida = DMF_UTIL_Valida(UnidadeMedida, 3, TipoDadoDaruma.Alfanumerico);
        TamMinDescItem = DMF_UTIL_Valida(TamMinDescItem, 2, TipoDadoDaruma.Numerico);
        CodigoItem = DMF_UTIL_Valida(CodigoItem, 14, TipoDadoDaruma.Alfanumerico);
        
        CargaTributaria = CargaTributaria.replace(",", "");
        String ind = DMF_UTIL_ConsultaIndice_Aliquota(CargaTributaria);

        String AD = DMF_UTIL_ConsultaTipoAcrescimoDecrescimo(TipoDescAcresc);
        if( AD.equals("0") || AD.equals("2")  )
            ValorDescAcresc= DMF_UTIL_Valida(ValorDescAcresc, 11, TipoDadoDaruma.Porcentagem);
        else
            ValorDescAcresc= DMF_UTIL_Valida(ValorDescAcresc, 11, TipoDadoDaruma.Numerico);

        String param = ind+Quantidade+PrecoUnitario+AD+ValorDescAcresc+TamMinDescItem+CodigoItem+UnidadeMedida+TruncarOuArredondar+DescricaoItem;

		filaMetodos.add(MetodoExecutando.iCFVender_ECF_DarumaAsync);
        darumaMobile.enviarComando_FS_F_Async( String.valueOf( cod) + param);
    }

    /// <summary>
    /// Este método totaliza o cupom fiscal. 
    /// </summary>
    /// <param name="TipoDescAcresc">Tipo Acréscimo ou Desconto -  Exemplo -  
    /// A% - Acréscimo em Percentual  
    /// A$ - Acréscimo em Valor 
    /// D% - Desconto em Percentual 
    /// D$ - Desconto em Valor
    /// 2 caracteres. Alfanumerico.</param>
    /// <param name="ValorDescAcresc">Valor do acréscimo ou Valor da porcentagem. 11 caracteres. Real.</param>
    public void iCFTotalizarCupom_ECF_DarumaAsync(String TipoDescAcresc, String ValorDescAcresc)
    {
        int cod = 206;

        String AD = DMF_UTIL_ConsultaTipoAcrescimoDecrescimo(TipoDescAcresc);
        if (AD.equals("0") || AD.equals("2"))
            ValorDescAcresc = DMF_UTIL_Valida(ValorDescAcresc, 12, TipoDadoDaruma.Porcentagem);
        else
            ValorDescAcresc = DMF_UTIL_Valida(ValorDescAcresc, 12, TipoDadoDaruma.Numerico);

        String param = AD + ValorDescAcresc;
		filaMetodos.add(MetodoExecutando.iCFTotalizarCupom_ECF_DarumaAsync);	
        darumaMobile.enviarComando_FS_F_Async(String.valueOf(cod) + param);
    }

    /// <summary>
    /// Este método processa o pagamento do cupom fiscal. 
    /// </summary>
    /// <param name="FormaPgto">Descrição da forma de pagamento. 20 caracteres. Alfanumerico. </param>
    /// <param name="Valor">Valor da forma de pagamento. 12 caracteres. Numerico. 0 (zero) indica restante.</param>
    /// <param name="InfoAdicional">Informação Adicional. 84 caracteres. Alfanumerico.  </param>
    public void iCFEfetuarPagamento_ECF_DarumaAsync(String FormaPgto, String Valor, String InfoAdicional)
    {
        int cod = 209;

        String ind = DMF_UTIL_ConsultaIndice_MeiosPagto(FormaPgto);

        Valor = DMF_UTIL_Valida(Valor, 12, TipoDadoDaruma.Numerico);
        InfoAdicional = DMF_UTIL_Valida(InfoAdicional, 84, TipoDadoDaruma.AlfanumericoVariavel);

        String param = ind + Valor + InfoAdicional;
		filaMetodos.add(MetodoExecutando.iCFEfetuarPagamento_ECF_DarumaAsync);	
        darumaMobile.enviarComando_FS_F_Async(String.valueOf(cod) + param);
    }

    /// <summary>
    /// Este método finaliza o cupom fiscal, com a opção de emitir cupom adicional ou não com mensagem promocional. 
    /// </summary>
    /// <param name="CupomAdicional">
    /// 0 - Não Imprime Cupom Adicional 
    /// 1 - Imprime Cupom Adicional Simplificado 
    /// 2 - Imprime Cupom Adicional Detalhado 
    /// 3 - Imprime Cupom Adicional DLL 
    /// 1 caracter. Numerico.
    /// </param>
    /// <param name="Mensagem">Mensagem promocional em até 8 linhas. 384 caracteres. Alfanumerico.</param>
    public void iCFEncerrar_ECF_DarumaAsync(String CupomAdicional, String Mensagem)
    {
        int cod = 210;

        CupomAdicional = DMF_UTIL_Valida(CupomAdicional, 1, TipoDadoDaruma.Numerico);
        Mensagem = DMF_UTIL_Valida(Mensagem, 384, TipoDadoDaruma.AlfanumericoVariavel);

        String param = CupomAdicional + Mensagem;
		filaMetodos.add(MetodoExecutando.iCFEncerrar_ECF_DarumaAsync);	
        darumaMobile.enviarComando_FS_F_Async(String.valueOf(cod) + param);
    }

    /** 
     * Este método permite que você saiba qual é o número de casas decimais que está programado para a quantidade. 
     */
    public void rLerDecimaisQuantidade_ECF_DarumaAsync()
    {
        int cod = 200;
        String param = "139";

		filaMetodos.add(MetodoExecutando.rLerDecimaisQuantidade_ECF_DarumaAsync);		
		darumaMobile.enviarComando_FS_R_Async(String.valueOf(cod) + param);    
    }

	/**
	 * Este método permite que você saiba qual é o número de casas decimais que está programado para o valor dos itens. 
	 * @param retorno
	 * @param cod
	 * @param param
	 */
    public void rLerDecimaisValorUnitario_ECF_DarumaAsync()
    {
        int cod = 200;
        String param = "139";

		filaMetodos.add(MetodoExecutando.rLerDecimaisValorUnitario_ECF_DarumaAsync);	
		darumaMobile.enviarComando_FS_R_Async(String.valueOf(cod) + param);    

    }

	/**
     * Este método retorna as alíquotas cadastradas na impressora, separados por ponto-e-virgula (;).
     * Exemplo de retorno: T0700;T1200;S0700;T0800;T0900;S0800;T0800;T0900;T1000;T1100;T1200;T1300;T1400; 
     * T - Alíquota de ICMS  - Ex: 01700 - Alíquota de 17,00 de ICMS 
     * S - Alíquota de ISS -   Ex: 11700 - Alíquota de 17,00 de ISS 
     */
    public void rLerAliquotas_ECF_DarumaAsync()
    {
        int cod = 200;
        String param = "125";

		filaMetodos.add(MetodoExecutando.rLerAliquotas_ECF_DarumaAsync);	
		darumaMobile.enviarComando_FS_R_Async(String.valueOf(cod) + param); 
    }
    
    /**
     * Este método retorna os meios de pagamentos cadastrados na impressora, separados por virgula (,).
     * Exemplo de retorno: Dinheiro,  Duplicata, Cheque, Cartão 
     */
    public void rLerMeiosPagto_ECF_DarumaAsync()
    {
        int cod = 200;
        String param = "126";

		filaMetodos.add(MetodoExecutando.rLerMeiosPagto_ECF_DarumaAsync);	
		darumaMobile.enviarComando_FS_R_Async(String.valueOf(cod) + param); 
    }
    
    /**
     * Este método retorna os nomes dos totalizadores não fiscais em sua impressora, separados por virgula (,). 
     * Exemplo de retorno: Sangria, Suprimento, Conta de Luz, Conta de Telefone.
     */
    public void rLerCNF_ECF_DarumaAsync()
    {
        int cod = 200;
        String param = "127";

		filaMetodos.add(MetodoExecutando.rLerCNF_ECF_DarumaAsync);	
		darumaMobile.enviarComando_FS_R_Async(String.valueOf(cod) + param); 
    }

    /**
     * Relatórios Gerenciais cadastrados impressora Fiscal. 
     */
    public void rLerRG_ECF_DarumaAsync()
    {
        int cod = 200;
        String param = "128";

		filaMetodos.add(MetodoExecutando.rLerRG_ECF_DarumaAsync);	
		darumaMobile.enviarComando_FS_R_Async(String.valueOf(cod) + param); 
    }

	/**
	 * Carrega as tabelas de TOtalizadores Fiscais, Não Fiscais e Relatorios Gerais.
	 */
    public void DMF_UTIL_PreencheTabelas()
    {		
        rLerAliquotas_ECF_DarumaAsync();

        rLerMeiosPagto_ECF_DarumaAsync();

        rLerCNF_ECF_DarumaAsync();

        rLerRG_ECF_DarumaAsync();
    }
    
    //GRUPO Retorna Informação

    /**
     * Este método processa o pagamento do cupom fiscal. 
     * @param indice Código da informação que deseja obter da impressora.
     * Para recuperar mais de uma informação é necessário utilizar o  "+", dividindo os códigos das informações desejadas.
     * Exemplo: "78+1+140" 
     * @param separador se for “0” não utilizar separador ou seja, retornar informação sem separar 
     */
    public void rRetornarInformacao_ECF_DarumaAsync(String indice, String separador)
    {
        int cod = 200;

        DMF_UTIL_Valida(separador, 1, TipoDadoDaruma.Alfanumerico);
        indice = DMF_UTIL_Valida(indice, 3, TipoDadoDaruma.Numerico);

        for (String i : indice.split("\\+"))
        {
            String param = i;
            
    		filaMetodos.add(MetodoExecutando.rRetornarInformacao_ECF_DarumaAsync);
            darumaMobile.enviarComando_FS_R_Async(String.valueOf(cod) + param);
        }

    }
	
	 /**
     * Este método retorna o número de série da impressora. 
     * @param separador se for “0” não utilizar separador ou seja, retornar informação sem separar 
     */
    public void rRetornarNumeroSerie_ECF_DarumaAsync(String separador)
    {
        int cod = 200;

        DMF_UTIL_Valida(separador, 1, TipoDadoDaruma.Alfanumerico);
        indice = DMF_UTIL_Valida("78", 3, TipoDadoDaruma.Numerico);

        for (String i : indice.split("\\+"))
        {
            String param = i;
            
    		filaMetodos.add(MetodoExecutando.rRetornarInformacao_ECF_DarumaAsync);
            darumaMobile.enviarComando_FS_R_Async(String.valueOf(cod) + param);
        }

    }
    
    /** Método que possibilita envio de texto a serem impressos, permitindo formatação do mesmo, através das tags listadas mais abaixo, que chamamos de D-HTML, por ser semelhante à programação HTML.
    	Com o uso deste método é possível enviar impressões linha a linha ou em blocos de linhas (buffer) de acordo com sua necessidade.
    	Antes de utiliza-lo pela primeira vez na aplicação é importante que tenham sido configurados os parâmetros de comunicação da impressora, como Porta de Comunicação e Velocidade, para que os dados sejam enviados a impressora corretamente.
    	
     	@string Variável string com o texto e a(s) tag(s) que serão usadas em até 2000 caracteres.
    
    	@Tam Tamanho do texto que será impresso. Se o valor "0"(zero) for informado, a DarumaFramework calcula o tamanho automaticamente pra você.
     */
    public void iImprimirTexto_DUAL_DarumaAsync(String string, int tam )
    {
        D_HTMLParser parser = new D_HTMLParser();

		filaMetodos.add(MetodoExecutando.iImprimirTexto_DUAL_DarumaAsync);
        darumaMobile.enviarComandoAsync(parser.Parse(string));
    }

    //Callback geral para tratar os retornos da Daruma Mobile Async
    
    /**
     * 
     */
    @Override
	public synchronized void callback(ActionEventDarumaMobile evento) {

    	if(filaMetodos.peek() == MetodoExecutando.inicializa ){
    		filaMetodos.pop();
    		callback_inicializa();
    	}
    	
    	
    	MetodoExecutando executando = filaMetodos.pop();
    	
    	
    	if(evento.getRetorno() == -1){
    		callback_Error(evento.getDarumaError());
    		Log.e(TAG, "erro - " + evento.getDarumaError());
    		return;
    	}
    	
    	
    	switch (executando) {
			case rLerDecimaisQuantidade_ECF_DarumaAsync:
				callback_rLerDecimaisQuantidade_ECF_DarumaAsync( String.valueOf(evento.getResposta()), evento.getCodigo(), evento.getParametros());
				break;

			case rLerDecimaisValorUnitario_ECF_DarumaAsync:
				callback_rLerDecimaisValorUnitario_ECF_DarumaAsync( String.valueOf(evento.getResposta()), evento.getCodigo(), evento.getParametros());
				break;

			case rLerAliquotas_ECF_DarumaAsync:
				callback_rLerAliquotas_ECF_DarumaAsync( String.valueOf(evento.getResposta()), evento.getCodigo(), evento.getParametros());
				break;

			case rLerMeiosPagto_ECF_DarumaAsync:
				callback_rLerMeiosPagto_ECF_DarumaAsync( String.valueOf(evento.getResposta()), evento.getCodigo(), evento.getParametros());
				break;

			case rLerCNF_ECF_DarumaAsync:
				callback_rLerCNF_ECF_DarumaAsync( String.valueOf(evento.getResposta()), evento.getCodigo(), evento.getParametros());
				break;

			case rLerRG_ECF_DarumaAsync:
				callback_rLerRG_ECF_DarumaAsync( String.valueOf(evento.getResposta()), evento.getCodigo(), evento.getParametros());
				break;
				

			case rRetornarInformacao_ECF_DarumaAsync:
				callback_rRetornarInformacao_ECF_DarumaAsync( String.valueOf(evento.getResposta()), evento.getCodigo(), evento.getParametros());
				break;
				
			case iCFAbrir_ECF_DarumaAsync:
				callback_iCFAbrir_ECF_DarumaAsync( String.valueOf(evento.getResposta()), evento.getCodigo(), evento.getParametros());
				break;
			case iCFEfetuarPagamento_ECF_DarumaAsync:
				callback_iCFEfetuarPagamento_ECF_DarumaAsync( String.valueOf(evento.getResposta()), evento.getCodigo(), evento.getParametros());
				break;
			case iCFTotalizarCupom_ECF_DarumaAsync: 
				callback_iCFTotalizarCupom_ECF_DarumaAsync( String.valueOf(evento.getResposta()), evento.getCodigo(), evento.getParametros());
				break;
			case iCFEncerrar_ECF_DarumaAsync:
				callback_iCFEncerrar_ECF_DarumaAsync( String.valueOf(evento.getResposta()), evento.getCodigo(), evento.getParametros());
				break;
			case iCFVender_ECF_DarumaAsync:
				callback_iCFVender_ECF_DarumaAsync( String.valueOf(evento.getResposta()), evento.getCodigo(), evento.getParametros());
				break;
				
			case fecharComunicacaoAsync:
				callback_fecharComunicacaoAsync( );
				break;

			case confParametrosAsync:

				break;
				
			default:
				break;
		}
    	
    	Log.d(TAG, "executando " + executando );
	}
	
	private void callback_fecharComunicacaoAsync() {
		listener.onComplete_fecharComunicacaoAsync();
	}

	private void callback_Error(String string) {
		listener.onError(string);
	}

	//Funções de Callback para quando o metodo retornar
	
    private void callback_iCFVender_ECF_DarumaAsync(String retorno, int cod,
			String param) {
        retorno = DMF_UTIL_FormataResposta(retorno, cod, param);

        iNItemVendido = retorno.substring(0, 3);
        iTipoDescontoItemVendido = retorno.substring(3, 3 + 1);
        iTotalLiquidoItemVendido = retorno.substring(4, 4 + 11);
        
        listener.onComplete_iCFVender_ECF_DarumaAsync();
		
	}

	private void callback_iCFEncerrar_ECF_DarumaAsync(String retorno,
			int cod, String param) {
        retorno = DMF_UTIL_FormataResposta(retorno, cod, param);

        iCCOCupomEncerrado = retorno.substring(0, 6);
        iTotalLiquidoCupomEncerrado = retorno.substring(6, 6 + 12);
        
        listener.onComplete_iCFEncerrar_ECF_DarumaAsync();
		
	}

	private void callback_iCFTotalizarCupom_ECF_DarumaAsync(String retorno,
			int cod, String param) {
        retorno = DMF_UTIL_FormataResposta(retorno, cod, param);

        iSubTotalCupomTotalizado = retorno.substring(0, 12);
        
        listener.onComplete_iCFTotalizarCupom_ECF_DarumaAsync();
		
	}

	private void callback_iCFEfetuarPagamento_ECF_DarumaAsync(String retorno,
			int cod, String param) {
        retorno = DMF_UTIL_FormataResposta(retorno, cod, param);

        //ESPECIAL, tem o + ou - no começo!
        iSubTotalCupomTotalizado = retorno.substring(0, 13);
        
        listener.onComplete_iCFEfetuarPagamento_ECF_DarumaAsync();
		
	}

	private void callback_iCFAbrir_ECF_DarumaAsync(String retorno, int cod,
			String param) {
        retorno = DMF_UTIL_FormataResposta(retorno, cod, param);

        iCOOCupomAberto = retorno.substring(0, 6);
        iCCFCupomAberto = retorno.substring(6, 6 + 6);
        
        listener.onComplete_iCFAbrir_ECF_DarumaAsync();
		
	}

	/**
     * Avisa ao usuário da Camada Developer que o loading terminou.
     */
	private void callback_inicializa() {
        listener.onComplete_inicializa_DarumaAsync();
	}

	/**
	 * 
	 * @param retorno
	 * @param cod
	 * @param param
	 */
    private synchronized void callback_rLerDecimaisQuantidade_ECF_DarumaAsync(String retorno, int cod, String param)
    {
        retorno = DMF_UTIL_FormataResposta(retorno, cod, param);

        int valor = 0;

        try{
        	valor = Integer.parseInt( String.valueOf(retorno.charAt(0)) );
        }
        catch(NumberFormatException ex){
        	 DMF_UTIL_LancaException(-5);
        }
        
        this.decimaisQuantidade = valor;
        
        listener.onComplete_rLerDecimaisQuantidade_ECF_DarumaAsync(valor);
    }

	/**
	 * 
	 * @param retorno
	 * @param cod
	 * @param param
	 */
    private synchronized void callback_rLerDecimaisValorUnitario_ECF_DarumaAsync(String retorno, int cod, String param)
    {
        retorno = DMF_UTIL_FormataResposta(retorno, cod, param);

        int valor = 0;

        try{
        	valor = Integer.parseInt( String.valueOf(retorno.charAt(1)) );
        }
        catch(NumberFormatException ex){
        	 DMF_UTIL_LancaException(-5);
        }
        
        this.decimaisValorUn = valor;
        
        listener.onComplete_rLerDecimaisValorUnitario_ECF_DarumaAsync(valor);
    }

	/**
	 * 
	 * @param retorno
	 * @param cod
	 * @param param
	 */
    public synchronized void callback_rLerAliquotas_ECF_DarumaAsync(String retorno, int cod, String param)
    {
        retorno = DMF_UTIL_FormataResposta(retorno, cod, param);
        
        String str = "";

        ArrayList<String> itens = DMF_UTIL_QuebraString(retorno, 5, 16);
        
        for(String s : itens){
        	char c = s.charAt(0);
        	
        	if (c != 255)
            {
                //separa a letra S ou T
                if (c == '0') str += 'T';
                else if (c == '1') str += 'S';

                //separa o número de 4 digitos
                str += s.substring(1) + ';';
            }
            else
            {
                //fim
                break;
            }
        }
                
        //Remove o ponto e virgula do final
        if(str.length() > 0)
            str = str.substring(0, str.length()-1);

        //Atualiza lista de aliquotas
        aliquotas.clear();
        aliquotas.addAll( Arrays.asList( str.split(";") ) );

        String end = ";TF1;TF2;TI1;TI2;TN1;TN2;SFS1;SFS2;SIS1;SIS2;SNS1;SNS2";
        String result = str + end;

        listener.onComplete_rLerAliquotas_ECF_DarumaAsync(result);
    }

	/**
	 * 
	 * @param retorno
	 * @param cod
	 * @param param
	 */
    public synchronized void callback_rLerMeiosPagto_ECF_DarumaAsync(String retorno, int cod, String param)
    {
        retorno = DMF_UTIL_FormataResposta(retorno, cod, param);
        retorno = DMF_UTIL_Limpa255(retorno);

        String str = "";

        ArrayList<String> itens = DMF_UTIL_QuebraString(retorno, 15, 20);
        
        for(String s : itens){
        	str += s + ',' ;
        }
        
        //remove a ultima virgula, 
        str = str.substring(0, str.length()-1);

        //Atualiza lista de de Meios de Pagamento
        meiosPagto.clear();
        meiosPagto.addAll( Arrays.asList( str.split(",") ) );

        listener.onComplete_rLerMeiosPagto_ECF_DarumaAsync(str);
    }

	/**
	 * 
	 * @param retorno
	 * @param cod
	 * @param param
	 */
    public synchronized void callback_rLerCNF_ECF_DarumaAsync(String retorno, int cod, String param)
    {
        retorno = DMF_UTIL_FormataResposta(retorno, cod, param);
        retorno = DMF_UTIL_Limpa255(retorno);

        String str = "";

        ArrayList<String> itens = DMF_UTIL_QuebraString(retorno, 15, 20);
        
        for(String s : itens){
        	str += s + ',' ;
        }
        

        //remove a ultima virgula, 
        str = str.substring(0, str.length()-1);

        //Atualiza lista de de Meios de Pagamento
        naoFiscais.clear();
        naoFiscais.addAll( Arrays.asList( str.split(",") ) );

        listener.onComplete_rLerCNF_ECF_DarumaAsync(str);
    }

	/**
	 * 
	 * @param retorno
	 * @param cod
	 * @param param
	 */
    public synchronized void callback_rLerRG_ECF_DarumaAsync(String retorno, int cod, String param)
    {
        retorno = DMF_UTIL_FormataResposta(retorno, cod, param);
        retorno = DMF_UTIL_Limpa255(retorno);

        String str = "";

        ArrayList<String> itens = DMF_UTIL_QuebraString(retorno, 15, 20);
        
        for(String s : itens){
        	str += s + ',' ;
        }
        
        //remove a ultima virgula, 
        str = str.substring(0, str.length() - 1);

        //Atualiza lista de de Meios de Pagamento
        relatoriosGerais.clear();
        relatoriosGerais.addAll( Arrays.asList( str.split(",") ) );

        listener.onComplete_rLerRG_ECF_DarumaAsync(str);
    }

	/**
	 * 
	 * @param retorno
	 * @param cod
	 * @param param
	 */
    public synchronized void callback_rRetornarInformacao_ECF_DarumaAsync(String retorno, int cod, String param)
    {
    	retorno = DMF_UTIL_FormataResposta(retorno, cod, param);

        listener.onComplete_rRetornarInformacao_ECF_DarumaAsync(retorno);
    }
    
	//Funções Auxliares

	/**
	 * Retorna um Array com tamnho = length que tem por base a String retorno. Cada string do Array terá tamanho size.
	 * @param retorno String a ser utilizada como base.
	 * @return Array de tamanho length
	 */
    public ArrayList<String> DMF_UTIL_QuebraString(String retorno, int size, int length)
    {
    	ArrayList<String> ret = new ArrayList<String>();
    	
    	retorno = DMF_UTIL_Limpa255(retorno);
    	
    	for(int cont = 0; cont < length; cont++){
    		
    		String temp = retorno.substring(cont*size, (cont*size) + size );
    		temp = temp.trim();
    		
    		if(temp.length()>0)
    			ret.add( temp );    		
    	}
    	
        return ret;
    }
    
	/**
	 * Retorna uma String espaços no lugar do caracter 255.
	 * @param retorno String a ser utilizada como base.
	 * @return String sem caracteres 255 para exibição
	 */
    public String DMF_UTIL_Limpa255(String retorno)
    {
        return retorno.replace(((char)255), ' ');
    }

    /**
     * Retorna uma String com zeros (0) preenchidos à esquerda.
     * @param str String a ser utilizada como base.
     * @param tam Tamanho total que a String deverá ter depois de preenchida.
     * @return String com espaços preenchidos até completar o tamanho desejado.
     */
    public String DMF_UTIL_PreencheZerosEsquerda(String str, int tam) 
    {
    	while (str.length() < tam){
    		str = '0' + str;
    	}
        return str;
    }

    /**
     * Retorna uma String com zeros (0) preenchidos à direita.
     * @param str String a ser utilizada como base.
     * @param tam Tamanho total que a String deverá ter depois de preenchida.
     * @return String com espaços preenchidos até completar o tamanho desejado.
     */
    public String DMF_UTIL_PreencheZerosDireita(String str, int tam)
    {
    	while (str.length() < tam){
    		str += '0';
    	}
        return str;       
    }

    /**
     * Retorna uma String com espaços ( ) preenchidos à esquerda.
     * @param str String a ser utilizada como base.
     * @param tam Tamanho total que a String deverá ter depois de preenchida.
     * @return String com espaços preenchidos até completar o tamanho desejado.
     */
    public String DMF_UTIL_PreencheEspacosEsquerda(String str, int tam)
    {
    	while (str.length() < tam){
    		str = ' ' + str;
    	}
        return str;
    }

    /**
     * Retorna uma String com espaços ( ) preenchidos à direita.
     * @param str String a ser utilizada como base.
     * @param tam Tamanho total que a String deverá ter depois de preenchida.
     * @return String com espaços preenchidos até completar o tamanho desejado.
     */
    public String DMF_UTIL_PreencheEspacosDireita(String str, int tam)
    {
    	while (str.length() < tam){
    		str += ' ';
    	}
        return str;
    }

    /**
     * Retorna uma String com o caracter 255 no final.
     * @param str String a ser utilizada como base.
     * @return String com 255 no final
     */
    public String DMF_UTIL_FechaStringCom255(String str)
    {
        return str + (char)255;
    }

	/**
	 * Lança exceptions para ser tratado pelo usuário na Thread de UI
	 * @param cod Código do Erro a ser lançado.
	 * @throws DarumaException
	 */
	public void DMF_UTIL_LancaException(int cod) throws DarumaException
    {
        DarumaException ex;
        String errorMessage = "";

        if (erros.get(cod) == null)
        {
            ex = new DarumaException(errorMessage);
        }
        else
        {
            ex = new DarumaException("Erro não identificado.");
        }

        throw ex;
    }
	
	/**
	 * Retira [:], confimarção do comando/parametros e [CR] da String de retorno e lança exceções se for identificado algum erro!
	 * @param resp Resposta do comando
	 * @param cod Codigo do comando a ser executado
	 * @param param Parametros utilizados pelo comando.
	 * @return Se não lançar uma Daruma Exception, retorna o resultado que deve ser entregue ao desenvolvedor salvo algumas exceções.
	 */
    public String DMF_UTIL_FormataResposta(String resp, int cod, String param)
    {
        resp = DMF_UTIL_LimpaCR(resp);

        //retira a confirmação
        int ind = resp.indexOf( (char) cod );
        
        String message = "";
        if(ind != -1)
        	message = resp.substring(0, ind);
        else 
        	DMF_UTIL_LancaException(-5);

        //se possuir uma mensagem de erro e aviso
        if(message.length() > 0)
        {
            //Extrai o erro no formato novo
            String strCodErr = message.substring(2, 2 + 3);
            //extrai aviso
            String strCodAvi = message.substring(5, 5 + 2);

            int codErr = Integer.parseInt( strCodErr );
            int codAvi = Integer.parseInt( strCodAvi );

            //Lança Exceção
            if (codErr != 0)
            {
                DMF_UTIL_LancaException(codErr);
            }

            //Armazena o ultimo erro e o ultimo aviso
            codigoUltimoAviso = codAvi;
            codigoUltimoErro = codErr;
        }
        //monta a mensagem de confirmação
        String confirmacao = message + ((char)cod) + param;
        String retorno = resp.substring(confirmacao.length());
        
        return retorno;
    }

    /**
     * Retira [:] e [CR] da String de retorno!
     * @param resp Resposta do comando
     * @return A String de retorno, limpa.
     */
    public String DMF_UTIL_LimpaCR(String resp)
    {
        //remove o char '[checksum]:' e o no final '[CR]'
        String retorno = resp.substring( resp.indexOf(':')+1 );
        retorno = retorno.substring(0, retorno.length() - 1);

        return retorno;
    }

    /**
     * Valida string baseada na quantidade de caracteres e no tipo.
     * @param str String a ser utilizada como base
     * @param tam Tamanho total que a string deverá ter.
     * @param t Indica se deverá ser alfanumerica, numerica comum, valor unitario, quantidade ou porcentagem
     * @return 
     */
    public String DMF_UTIL_Valida(String str, int tam, TipoDadoDaruma t)
    {
        if (str.length() > tam) DMF_UTIL_LancaException(-3);

        try{
	        if(t == TipoDadoDaruma.Numerico )
	        	Integer.parseInt(str);
	        if( ( t == TipoDadoDaruma.Porcentagem || t == TipoDadoDaruma.ValorUn || t == TipoDadoDaruma.Quantidade ) )
	        	Double.parseDouble(str);
        }
        catch(NumberFormatException ex){
            DMF_UTIL_LancaException(-4);
        }
        
        int decimaisAtuais = 0;
        int decimaisAdicionais = 0;

        String result = "";

        switch (t)
        {
            case Alfanumerico:
                result = DMF_UTIL_PreencheEspacosDireita(str,tam);
                break;
            case AlfanumericoVariavel:
                result = DMF_UTIL_FechaStringCom255(str);
                break;
            case Numerico:
                result = DMF_UTIL_PreencheZerosEsquerda(str, tam);
                break;
            case ValorUn:
                if (str.indexOf(",") != -1)
                {
                    decimaisAtuais = str.length() - (str.indexOf(",") + 1);
                    decimaisAdicionais = decimaisValorUn - decimaisAtuais;

                    //não pode ter mais que (decimaisQuantidade) casas antes da virgula
                    if (decimaisAdicionais < 0) DMF_UTIL_LancaException(-3);

                    //regulariza a quantidade de decimais
                    result = DMF_UTIL_PreencheZerosDireita(str, str.length() + decimaisAdicionais);
                    //Remove a virgula
                    result.replace(",", "");
                }
                else
                {
                    //regulariza a quantidade de decimais
                    if (decimaisValorUn > 2)
                        result = DMF_UTIL_PreencheZerosDireita(str, str.length() + decimaisValorUn - 2);
                    else
                        result = str;
                }

                if (str.length() > tam)
                    result = result.substring(decimaisValorUn);

                //regulariza o resto da string
                result = DMF_UTIL_PreencheZerosEsquerda(result, tam);
                break;
            case Quantidade:
                if (str.indexOf(",") != -1)
                {
                    decimaisAtuais = str.length() - (str.indexOf(",") + 1);
                    decimaisAdicionais = decimaisQuantidade - decimaisAtuais;

                    //não pode ter mais que (decimaisQuantidade) casas antes da virgula
                    if (decimaisAdicionais < 0) DMF_UTIL_LancaException(-3);

                    //regulariza a quantidade de decimais
                    result = DMF_UTIL_PreencheZerosDireita(str, str.length() + decimaisAdicionais);
                    //Remove a virgula
                    result.replace(",", "");
                }
                else
                {
                    //regulariza a quantidade de decimais
                    result = DMF_UTIL_PreencheZerosDireita(str, str.length() + decimaisQuantidade);
                    
                }

                if (str.length() > tam)
                    result = result.substring(decimaisQuantidade);

                //regulariza o resto da string
                result = DMF_UTIL_PreencheZerosEsquerda(result, tam);
                break;
            case Porcentagem:

                if (str.indexOf(",") != -1)
                {
                    decimaisAtuais = str.length() - (str.indexOf(",") + 1);
                    decimaisAdicionais = 2 - decimaisAtuais;

                    //não pode ter mais que duas casas antes da virgula
                    if (decimaisAdicionais < 0 ) DMF_UTIL_LancaException(-3);

                    result = str.substring(0, str.indexOf(",") + decimaisAtuais);

                    //regulariza a quantidade de decimais
                    result = DMF_UTIL_PreencheZerosDireita(result, result.length() + decimaisAdicionais);
                    //regulariza a quantidade de inteiros
                    result = DMF_UTIL_PreencheZerosEsquerda(result, 5);
                    //Remove a virgula
                    result.replace(",", "");
                }
                else
                {
                    if(str.length() >  4)
                        result = str.substring(0, 4);
                    else
                        //regulariza a quantidade de inteiros
                        result = DMF_UTIL_PreencheZerosEsquerda(str, 4);
                }

                //regulariza o resto da string
                result = DMF_UTIL_PreencheZerosDireita(result, tam);
                break;
        }
        
        return result;

    }
            

    /// <summary>
    /// função auxiliar para consultar tipos de acrescimos ou decrescimos pois C# não suporta $ ou % em um enum.
    /// </summary>
    /// <param name="str">String contendo A$, A%, D$ ou D%</param>
    public String DMF_UTIL_ConsultaTipoAcrescimoDecrescimo(String str)
    {
        int ind = tiposAcrescimosDecrescimos.indexOf(str);

        if (ind == -1)
        {
            //Erro de Aliquota não encontrada
            DMF_UTIL_LancaException(-2);
        }

        return String.valueOf(ind);
    }

    /// <summary>
    /// Retorna o indice (int) de uma alíquota para consulta posterior.
    /// </summary>
    /// <param name="str">Descrição da Aliquota, por Ex. 010800 .</param>
    public String  DMF_UTIL_ConsultaIndice_Aliquota(String str)
    {
        int ind = aliquotas.indexOf(str);

        if (ind == -1)
        {
            ind = aliquotasAux.indexOf(str);

            if (ind == -1)
            {
                //Erro de Aliquota não encontrada
                DMF_UTIL_LancaException(-2);
            }

            ind += 16;
        }

        return DMF_UTIL_PreencheZerosEsquerda(String.valueOf(ind +1), 2);
    }

    /// <summary>
    /// Retorna o indice (String) de um Meio de Pagamento para consulta posterior.
    /// </summary>
    /// <param name="str">Descrição do Meio de Pagamento, por Ex. Dinheiro.</param>
    public String DMF_UTIL_ConsultaIndice_MeiosPagto(String str)
    {
        int ind = meiosPagto.indexOf(str);

        if (ind == -1)
        {
            //Erro de Aliquota não encontrada
            DMF_UTIL_LancaException(-2);
        }

        return DMF_UTIL_PreencheZerosEsquerda(String.valueOf(ind +1), 2);
    }

    /// <summary>
    /// Retorna o indice (String) de um totalizador não fiscal para consulta posterior.
    /// </summary>
    /// <param name="str">Descrição do totalizador, por Ex. Dinheiro.</param>
    public String DMF_UTIL_ConsultaIndice_CNF(String str)
    {
        int ind = naoFiscais.indexOf(str);

        if (ind == -1)
        {
            //Erro de Aliquota não encontrada
            DMF_UTIL_LancaException(-2);
        }

        return DMF_UTIL_PreencheZerosEsquerda(String.valueOf(ind +1), 2);
    }

    /// <summary>
    /// Retorna o indice (String) de um relatorio geral para consulta posterior.
    /// </summary>
    /// <param name="str">Descrição do totalizador, por Ex. Dinheiro.</param>
    public String DMF_UTIL_ConsultaIndice_RG(String str)
    {
        int ind = relatoriosGerais.indexOf(str);

        if (ind == -1)
        {
            //Erro de Aliquota não encontrada
            DMF_UTIL_LancaException(-2);
        }

        return DMF_UTIL_PreencheZerosEsquerda(String.valueOf(ind +1), 2);
    }


	
}
