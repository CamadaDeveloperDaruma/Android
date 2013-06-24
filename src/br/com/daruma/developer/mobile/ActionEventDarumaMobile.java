package br.com.daruma.developer.mobile;

public class ActionEventDarumaMobile {
	
	public static enum TipoComando {
		iniciarComunicacaoAsync,
		fecharComunicacaoAsync,
		enviarComandoAsync,
		respostaComandoAsync,
		enviarResponderComandoAsync,
		retornarParametrosAsync,
		retornarParametroFrameworkAsync,
		retornarParametroComunicacaoAsync,
		enviarComando_FS_Async,
		enviarComando_FS_F_Async,
		enviarComando_FS_R_Async,
		enviarComando_FS_C_Async,
		confParametrosAsync
	};
	
	private TipoComando tipoComando;
	
	/**
	 * Metodo para retornar o tipo do comando executado.
	 */
	public TipoComando getTipoComando(){ return this.tipoComando; }
	
	/**
	 * Metodo para configurar o tipo do comando executado.
	 */
	public void setTipoComando(TipoComando tipoComando) { this.tipoComando = tipoComando; }
	
	private int retorno = -1;
	
	/**
	 * Metodo que retorna o valor retornado pelo comando executado.
	 * TRATAR STATUS DA EXECU��O
	 */
	public int getRetorno(){ return this.retorno; }
	
	/**
	 * Metodo para configurar o valor retornado pelo comando executado.
	 * TRATAR STATUS DA EXECU��O
	 */
	public void setRetorno(int retorno) { this.retorno = retorno; }
	
	private char[] resposta = new char[1024];
	
	/**
	 * Metodo que retorna a resposta recebida pelo comando executado.
	 * TRATAR STATUS DA EXECU��O
	 */
	public char[] getResposta(){ return this.resposta; }
	
	/**
	 * Metodo que configura a resposta recebida pelo comando executado.
	 * TRATAR STATUS DA EXECU��O
	 */
	public void setResposta(char[] resposta) { this.resposta = resposta; }
	
	private String[] retornoParametros  = new String[512];
	
	/**
	 * Metodo que retorna a resposta recebida pelo comando executado (v�rios par�metros e logs).
	 * Para um �nico par�metro, veja @see {@link getRetornoParametro}
	 * TRATAR STATUS DA EXECU��O
	 */
	public String[] getRetornoParametros(){ return this.retornoParametros; }
	
	/**
	 * Metodo que configura a resposta recebida pelo comando executado (v�rios par�metros e logs). 
	 * Para um �nico par�metro, veja @see {@link setRetornoParametro}
	 * TRATAR STATUS DA EXECU��O
	 */
	public void setRetornoParametros(String[] retornoParametros) { this.retornoParametros = retornoParametros; }
	
	private String retornoParametro = new String();
	
	/**
	 * Metodo que retorna a resposta recebida pelo comando executado (um �nico par�metro).
	 * Para v�rios par�metros e/ou log, veja @see {@link getRetornoParametros}.
	 * @return String -> retorna a resposta do par�metro executado.
	 * Para v�rios par�metros e/ou log, veja @see {@link getRetornoParametros}.
	 * TRATAR STATUS DA EXECU��O
	 */
	public String getRetornoParametro(){ return this.retornoParametro; }
	
	/**
	 * Metodo que configura a resposta recebida pelo comando executado (um �nico par�metro).
	 * Para v�rios par�metros, veja @see {@link setRetornoParametros}.
	 * @param retornoParametro -> configura a resposta do par�metro executado.
	 * Para v�rios par�metros, veja @see {@link setRetornoParametros}.
	 * TRATAR STATUS DA EXECU��O
	 */
	public void setRetornoParametro(String retornoParametro) { this.retornoParametro = retornoParametro; }
	
	private boolean status = false;
	
	/**
	 * Metodo utilizado para retornar o status da execu��o do comando.
	 * OBS: Retorna somente se o m�todo foi executado com sucesso. � diferente do retorno.
	 * @return Um boolean que identifica se o comando executou/respondeu.
	 * TRUE -> Executou o comando e/ou obteve resposta.
	 * FALSE -> N�O executou o comando e/ou N�O obteve resposta.
	 */
	public boolean getStatus(){ return this.status; }
	
	/**
	 * Metodo que configura o status do comando executado.
	 * OBS: Retorna somente se o m�todo foi executado com sucesso. � diferente do retorno.
	 * @param status -> O status da execu��o do comando.
	 * TRUE -> Executou o comando e/ou obteve resposta.
	 * FALSE -> N�O executou o comando e/ou N�O obteve resposta.
	 */
	public void setStatus(boolean status) { this.status = status; }

	private int codigo = 0;
	
	/**
	 * 
	 */
	public int getCodigo(){ return this.codigo; }
	
	/**
	 * 
	 */
	public void setCodigo(int codigo) { this.codigo = codigo; }
	
	private String parametros = "";
	
	/**
	 * 
	 */
	public String getParametros(){ return this.parametros; }
	
	/**
	 * 
	 */
	public void setParametros(String parametros) { this.parametros = parametros; }
	
	
	
	
	public ActionEventDarumaMobile() { }
	
	public ActionEventDarumaMobile(TipoComando tipoComando) {
		this.tipoComando = tipoComando;
	}
	
	public ActionEventDarumaMobile(TipoComando tipoComando, int retorno) {
		this.tipoComando = tipoComando;
		this.retorno = retorno;
	}
	
	public ActionEventDarumaMobile(TipoComando tipoComando, char[] resposta) {
		this.tipoComando = tipoComando;
		this.resposta = resposta;
	}
	
	public ActionEventDarumaMobile(TipoComando tipoComando, String[] retornoParametros) {
		this.tipoComando = tipoComando;
		this.retornoParametros = retornoParametros;
	}
	
	public ActionEventDarumaMobile(TipoComando tipoComando, String retornoParametro) {
		this.tipoComando = tipoComando;
		this.retornoParametro = retornoParametro;
	}
	
	public ActionEventDarumaMobile(boolean status) {
		this.status = status;
	}

	String darumaError = "";
	
	public void setDarumaError(String message) {
		darumaError = message;
	}

	public String getDarumaError() {
		return darumaError;
	}
}
