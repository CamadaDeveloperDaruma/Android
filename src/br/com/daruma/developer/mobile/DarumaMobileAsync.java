package br.com.daruma.developer.mobile;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.os.AsyncTask;
import android.util.Log;
import br.com.daruma.framework.mobile.DarumaMobile;
import br.com.daruma.framework.mobile.exception.DarumaComunicacaoException;
import br.com.daruma.framework.mobile.exception.DarumaException;
import br.com.daruma.framework.mobile.log.DarumaLoggerConst;

public class DarumaMobileAsync {
	public static final String TAG = "DarumaMobileAsync";

	private DarumaAsync darumaAsync;
	
	private boolean trataExcecao = false;
	
	private static boolean verificaExcecao(String param) {
		int pos = param.toUpperCase(Locale.getDefault()).indexOf("TRATAEXCECAO");
		if(pos != -1) { //Default de tratamento de exceção eh false
			String par = param.substring(pos);
			par = par.substring(par.indexOf("=") + 1).toUpperCase(Locale.getDefault());
			
			if(par.toUpperCase(Locale.getDefault()).startsWith("TRUE")){ //mas se houver .. e for true
				return true;
			}
		}
		return false;
	}
	
	private DarumaMobileAsync(String nomeInstancia, String parametroInicializacao, OnDarumaMobileListener listener) throws DarumaException {
		try {
			trataExcecao = verificaExcecao(parametroInicializacao);  // verifica TRATAEXCECAO
			darumaAsync = new DarumaAsync(listener); // instancia a classe async
			darumaAsync.execute(nomeInstancia, parametroInicializacao);  // inicia a classe de execucao de comando em background (nova thread)
			
		} catch (Exception e) {
			//darumaM = null;
			throw new DarumaException("Ocorreu um erro ao inicializar assincronamente. ");
		}
	}
	
	/**
	 * 	Método de inicialização de instâncias de Fachada! (ela terá um nome padrão)
	 * @param paramInicializacao -> parametro de inicialização de contexto da fachada.
	 * @return Um novo Objeto de fachada com seu contexto inicializado.
	 * @throws DarumaComunicacaoException -> Caso haja algum erro de inicialização E trataexcecao=true.
	 */
	public static DarumaMobileAsync inicializarAsync(String parametroInicializacao, OnDarumaMobileListener listener) throws DarumaException {
		return inicializarAsync(DarumaLoggerConst.FRAMEWORK, parametroInicializacao, listener);
	}
	
	/**
	 * 	Método de inicialização de instâncias de Fachada! (ela terá um nome padrão)
	 * @param paramInicializacao -> parametro de inicialização de contexto da fachada.
	 * @return Um novo Objeto de fachada com seu contexto inicializado.
	 * @throws DarumaException -> Caso haja algum erro de inicialização E trataexcecao=true.
	 */
	public static DarumaMobileAsync inicializarAsync(String nomeInstancia, String parametroInicializacao, OnDarumaMobileListener listener) throws DarumaException {
		try {
			return new DarumaMobileAsync(nomeInstancia, parametroInicializacao, listener);
		} catch(DarumaException de) {
			if(verificaExcecao(parametroInicializacao)) {
				throw de;
			}
		} catch(Exception e) {
			if(verificaExcecao(parametroInicializacao)) {
				throw new DarumaException(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 	Método responsável por iniciar de fato a comunicação com o dispositivo configurado previamente no método initialize.
	 * @throws DarumaException -> Caso haja alguma falha E trataexcecao=true.
	 * @return Retorna -1 = não conseguiu, 0 se conseguiu enviar o comando.
	 */
	public int iniciarComunicacaoAsync() throws DarumaException {
		Dados dados = new Dados();
		dados.tipoMetodo = TipoComando.iniciarComunicacao;
		try {
			darumaAsync.blockingQueueMetodos.put(dados);
		} catch (InterruptedException e) {
			if(trataExcecao)
				throw new DarumaException(e.getMessage());
			return -1;
		}
		return 0;
	}
	
	/**
	 * 	Método responsável por iniciar de fato a comunicação com o dispositivo configurado previamente no método initialize.
	  * @throws DarumaException -> Caso haja alguma falha E trataexcecao=true.
	 * @return Retorna -1 = não conseguiu, 0 se conseguiu enviar o comando.
	 */
	public int fecharComunicacaoAsync() throws DarumaException {
		Dados dados = new Dados();
		dados.tipoMetodo = TipoComando.fecharComunicacao;
		try {
			darumaAsync.blockingQueueMetodos.put(dados);
		} catch (InterruptedException e) {
			if(trataExcecao)
				throw new DarumaException(e.getMessage());
			return -1;
		}
		return 0;
	}
	
	/**
	 * Método utilizado para calcular checkSum da string passada como parâmetro.
	 * @param comando -> Variavel com o comando que será tomado para checkSum
	 * @return Retorna uma String com o calculo de check sum valido.
	 */
	public String checkSum(String comando) throws DarumaException{
		return darumaAsync.getDarumaMobile().checkSum(comando);
	}
	
	/**
	 * 	Método que retorna os dispositivos que podem ser pareados via Bluetooth 
	 * @return
	 * @throws DarumaException -> Caso haja alguma falha no Bluetooth.
	 */
	public static List<String> retornaDispositivosBluetooth() throws DarumaException {
		return DarumaMobile.retornaDispositivosBluetooth();
	}
	
	/**
	 * 	Método que envia um comando.
	 * @param comando -> Comando que deve ser enviado.
	  * @throws DarumaException -> Caso haja alguma falha E trataexcecao=true.
	 * @return Retorna -1 = não conseguiu, 0 se conseguiu enviar o comando.
	 */
	public int enviarComandoAsync(String comando) throws DarumaException {
		Dados dados = new Dados();
		dados.tipoMetodo = TipoComando.enviarComando;
		dados.comando = comando;
		try {
			darumaAsync.blockingQueueMetodos.put(dados);
		} catch (InterruptedException e) {
			if(trataExcecao)
				throw new DarumaException(e.getMessage());
			return -1;
		}
		return 0;
	}
	
	/**
	 * 	Método que espera uma resposta do último comando enviado.
	 * @throws DarumaException -> Caso haja alguma falha E trataexcecao=true.
	 * @return Retorna -1 = não conseguiu, 0 se conseguiu enviar o comando.
	 */
	public int respostaComandoAsync() throws DarumaException {
		Dados dados = new Dados();
		dados.tipoMetodo = TipoComando.respostaComando;
		try {
			darumaAsync.blockingQueueMetodos.put(dados);
		} catch (InterruptedException e) {
			if(trataExcecao)
				throw new DarumaException(e.getMessage());
			return -1;
		}
		return 0;
	}
	
	/**
	 * 	Metodo para envia um comando e receber a resposta. Ele não faz calculo de check Sum
	 * @param comando -> comando a ser enviado
	 * @throws DarumaException -> Caso haja alguma falha E trataexcecao=true.
	 * @return Retorna -1 = não conseguiu, 0 se conseguiu enviar o comando.
	 */
	public int enviarResponderComandoAsync(String comando) throws DarumaException {
		Dados dados = new Dados();
		dados.tipoMetodo = TipoComando.enviarResponderComando;
		dados.comando = comando;
		try {
			darumaAsync.blockingQueueMetodos.put(dados);
		} catch (InterruptedException e) {
			if(trataExcecao)
				throw new DarumaException(e.getMessage());
			return -1;
		}
		return 0;
	}
	
	/**
	 * 	Metodo para retornar o status da conexão
	 * @throws DarumaException -> Caso haja alguma falha E trataexcecao=true.
	 * @return Retorna -1 = não está conectado, 0 se está conectado.
	 */
	public int retornarStatusConexao(){
		return darumaAsync.getDarumaMobile().retornarStatusConexao();
	}
	
	/**
	 * 	Método para retornar parâmetros genéricos.
	 * @param paramConf -> String com os parametros e configurações que serão retornados. Ex.: "@FRAMEWORK(TRATAEXCECAO);@SOCKET(NOME,HOST,PORT)"
	 * @throws DarumaException -> Caso haja alguma falha E trataexcecao=true.
	 * @return Retorna -1 = não conseguiu, 0 se conseguiu enviar o comando.
	 */
	public int retornarParametrosAsync(String paramConf) throws DarumaException {
		Dados dados = new Dados();
		dados.tipoMetodo = TipoComando.retornarParametros;
		dados.comando = paramConf;
		try {
			darumaAsync.blockingQueueMetodos.put(dados);
		} catch (InterruptedException e) {
			if(trataExcecao)
				throw new DarumaException(e.getMessage());
			return -1;
		}
		return 0;
	}
	
	/**
	 * 	Método para retornar um parâmetro de Framework.
	 * @param strParametro -> Parametro de framework valido.
	 * @throws DarumaException -> Caso haja alguma falha E trataexcecao=true.
	 * @return Retorna -1 = não conseguiu, 0 se conseguiu enviar o comando.
	 */
	public int retornarParametroFrameworkAsync(String strParametro) throws DarumaException {
		Dados dados = new Dados();
		dados.tipoMetodo = TipoComando.retornarParametroFramework;
		dados.comando = strParametro;
		try {
			darumaAsync.blockingQueueMetodos.put(dados);
		} catch (InterruptedException e) {
			if(trataExcecao)
				throw new DarumaException(e.getMessage());
			return -1;
		}
		return 0;
	}
	
	/**
	 * 	Método para retornar um parâmetro de Comunicacao.
	 * @param strParametro -> Parametro de framework valido.
	 * @throws DarumaException -> Caso haja alguma falha E trataexcecao=true.
	 * @return Retorna -1 = não conseguiu, 0 se conseguiu enviar o comando.
	 */
	public int retornarParametroComunicacaoAsync(String strParametro) throws DarumaException {
		Dados dados = new Dados();
		dados.tipoMetodo = TipoComando.retornarParametroComunicacao;
		dados.comando = strParametro;
		try {
			darumaAsync.blockingQueueMetodos.put(dados);
		} catch (InterruptedException e) {
			if(trataExcecao)
				throw new DarumaException(e.getMessage());
			return -1;
		}
		return 0;
	}
	
	/**
	 * 	Método utilizado para comunicação com ECF. 
	 * @param tipoComando -> Tipo de comando F, R ou C
	 * @param comando -> o comando que deseja enviar sem a necessidade do [FS][F, R ou C] no inicio dele.
	 * @throws DarumaException -> Caso haja alguma falha E trataexcecao=true.
	 * @return Retorna -1 = não conseguiu, 0 se conseguiu enviar o comando.
	 */
	public int enviarComando_FS_Async(String tipoComando, String comando) throws DarumaException {
		Dados dados = new Dados();
		dados.tipoMetodo = TipoComando.enviarComando_FS;
		dados.tipoComando = tipoComando;
		dados.comando = comando;
		try {
			darumaAsync.blockingQueueMetodos.put(dados);
		} catch (InterruptedException e) {
			if(trataExcecao)
				throw new DarumaException(e.getMessage());
			return -1;
		}
		return 0;
	}
	
	/**
	 * 	Método para enviar um comando [FS]F para ECF.
	 * @param comando -> o comando que deseja enviar sem a necessidade do [FS][F, R ou C] no inicio dele.
	 * @throws DarumaException -> Caso haja alguma falha E trataexcecao=true.
	 * @return Retorna -1 = não conseguiu, 0 se conseguiu enviar o comando.
	 */
	public int enviarComando_FS_F_Async(String comando) throws DarumaException {
		Dados dados = new Dados();
		dados.tipoMetodo = TipoComando.enviarComando_FS_F;
		dados.comando = comando;
		try {
			darumaAsync.blockingQueueMetodos.put(dados);
		} catch (InterruptedException e) {
			if(trataExcecao)
				throw new DarumaException(e.getMessage());
			return -1;
		}
		return 0;
	}
	
	/**
	 * 	Método para enviar um comando [FS]F para ECF.
	 * @param comando -> o comando que deseja enviar sem a necessidade do [FS][F, R ou C] no inicio dele.
	 * @throws DarumaException -> Caso haja alguma falha E trataexcecao=true.
	 * @return Retorna -1 = não conseguiu, 0 se conseguiu enviar o comando.
	 */
	public int enviarComando_FS_R_Async(String comando) throws DarumaException {
		Dados d = new Dados();
		d.comando = comando;
		d.tipoMetodo = TipoComando.enviarComando_FS_R;
		try {
			darumaAsync.blockingQueueMetodos.put(d);
		} catch (InterruptedException e) {
			if(trataExcecao)
				throw new DarumaException(e.getMessage());
			return -1;
		}
		return 0;		
	}
	
	/**
	 * 	Método para enviar um comando [FS]F para ECF.
	 * @param comando -> o comando que deseja enviar sem a necessidade do [FS][F, R ou C] no inicio dele.
	 * @throws DarumaException -> Caso haja alguma falha E trataexcecao=true.
	 * @return Retorna -1 = não conseguiu, 0 se conseguiu enviar o comando.
	 */
	public int enviarComando_FS_C_Async(String comando) throws DarumaException {
		Dados dados = new Dados();
		dados.tipoMetodo = TipoComando.enviarComando_FS_C;
		dados.comando = comando;
		try {
			darumaAsync.blockingQueueMetodos.put(dados);
		} catch (InterruptedException e) {
			if(trataExcecao)
				throw new DarumaException(e.getMessage());
			return -1;
		}
		return 0;
	}
	
	/**
	 * 	Metodo para incluir definições de configuração de FRAMEWORK.
	 * @param paramConf -> Parâmetros de configuração
	 * @throws DarumaException -> Caso haja alguma falha E trataexcecao=true.
	 * @return Retorna -1 = não conseguiu, 0 se conseguiu enviar o comando.
	 */
	public int confParametrosAsync(String paramConf) throws DarumaException {
		Dados dados = new Dados();
		dados.tipoMetodo = TipoComando.confParametros;
		dados.comando = paramConf;
		try {
			darumaAsync.blockingQueueMetodos.put(dados);
		} catch (InterruptedException e) {
			if(trataExcecao)
				throw new DarumaException(e.getMessage());
			return -1;
		}
		return 0;
	}


	public void finalizeAsync() {
		Dados dados = new Dados();
		dados.tipoMetodo = TipoComando.finalize;
		try {
			darumaAsync.blockingQueueMetodos.put(dados);
		} catch (InterruptedException e) {
			if(trataExcecao)
				throw new DarumaException(e.getMessage());
		}
	}
	
	/**
	 * 	Metodo para resetar o Log
	 * @params logs
	 */
	public int resetarLog() {
		return darumaAsync.getDarumaMobile().resetarLog();
	}
	
	/**
	 * Metodo para mostrar o Log
	 * @param logs
	 */
	public int mostrarLog(String[] logs){
		return darumaAsync.getDarumaMobile().mostrarLog(logs);
	}
	
	private class DarumaAsync extends AsyncTask<String, ActionEventDarumaMobile, Void> {
		private Dados dados = new Dados();
		
		private BlockingQueue<Dados> blockingQueueMetodos = new LinkedBlockingQueue<DarumaMobileAsync.Dados>();
		private DarumaMobile darumaMobile;
		
		public DarumaMobile getDarumaMobile() {
			return darumaMobile;
		}

		private OnDarumaMobileListener listener;
		
		public DarumaAsync(OnDarumaMobileListener listener) {
			this.listener = listener;
		}
		
		@Override
		protected Void doInBackground(String... params) {
			try {
				darumaMobile = DarumaMobile.inicializar(params[0], params[1]);
				
				try {
					boolean executa = true;

					while(executa) {
					
						dados = blockingQueueMetodos.take();
						
						ActionEventDarumaMobile evento = new ActionEventDarumaMobile();
						
						//ESSES METODOS NAO RECEBEM COMANDO, NAO PODE VERIFICAR ESSE DADO
						if(dados.tipoMetodo != TipoComando.iniciarComunicacao &&
								dados.tipoMetodo != TipoComando.fecharComunicacao &&
								dados.tipoMetodo != TipoComando.respostaComando) {
							
							//Separa o codigo do Comando
							try{
								evento.setCodigo(Integer.parseInt(dados.comando.substring(0, 3)));
							}
							catch (NumberFormatException ex){
								//TODO tratar exception
							}
							
							//Separa os parametros
							evento.setParametros(dados.comando.substring(3));
						}
						
						try {
							switch(dados.tipoMetodo) {
								case iniciarComunicacao: {
									evento.setTipoComando(ActionEventDarumaMobile.TipoComando.iniciarComunicacaoAsync);
									darumaMobile.iniciarComunicacao();
									evento.setRetorno(0);
									break;
								}
								case fecharComunicacao: {
									evento.setTipoComando(ActionEventDarumaMobile.TipoComando.fecharComunicacaoAsync);
									darumaMobile.fecharComunicacao();;
									evento.setRetorno(0);
									break;
								}
								case enviarComando: {
									evento.setTipoComando(ActionEventDarumaMobile.TipoComando.enviarComandoAsync);
									dados.retorno = darumaMobile.enviarComando(dados.comando);
									evento.setRetorno(dados.retorno);
									break;
								}
								case respostaComando: {
									evento.setTipoComando(ActionEventDarumaMobile.TipoComando.respostaComandoAsync);
									dados.retorno = darumaMobile.respostaComando(dados.resposta);
									evento.setRetorno(dados.retorno);
									evento.setResposta(dados.resposta);
									break;
								}
								case enviarResponderComando: {
									evento.setTipoComando(ActionEventDarumaMobile.TipoComando.enviarResponderComandoAsync);
									dados.retorno = darumaMobile.enviarResponderComando(dados.comando, dados.resposta);
									evento.setRetorno(dados.retorno);
									evento.setResposta(dados.resposta);
									break;
								}
								case retornarParametros: {
									evento.setTipoComando(ActionEventDarumaMobile.TipoComando.retornarParametrosAsync);
									dados.retorno = darumaMobile.retornarParametros(dados.comando, dados.retornoParametros);
									evento.setRetorno(dados.retorno);
									evento.setRetornoParametros(dados.retornoParametros);
									break;
								}
								case retornarParametroFramework: {
									evento.setTipoComando(ActionEventDarumaMobile.TipoComando.retornarParametroFrameworkAsync);
									dados.retornoParametros[0] = darumaMobile.retornarParametroFramework(dados.comando);
									evento.setRetornoParametro(dados.retornoParametros[0]);;
									evento.setRetorno(0);
									break;
								}
								case retornarParametroComunicacao: {
									evento.setTipoComando(ActionEventDarumaMobile.TipoComando.retornarParametroComunicacaoAsync);
									dados.retornoParametros[0] = darumaMobile.retornarParametroComunicacao(dados.comando);
									evento.setRetornoParametro(dados.retornoParametros[0]);
									break;
								}
								case enviarComando_FS: {
									evento.setTipoComando(ActionEventDarumaMobile.TipoComando.enviarComando_FS_Async); 
									dados.retorno = darumaMobile.enviarComando_FS(dados.tipoComando, dados.comando, dados.resposta);
									evento.setRetorno(dados.retorno);
									evento.setResposta(dados.resposta);
									break;
								}
								case enviarComando_FS_F: {
									evento.setTipoComando(ActionEventDarumaMobile.TipoComando.enviarComando_FS_F_Async);
									dados.retorno = darumaMobile.enviarComando_FS_F(dados.comando, dados.resposta);
									evento.setRetorno(dados.retorno);
									evento.setResposta(dados.resposta);
									break;
								}
								case enviarComando_FS_C: {
									evento.setTipoComando(ActionEventDarumaMobile.TipoComando.enviarComando_FS_C_Async);
									dados.retorno = darumaMobile.enviarComando_FS_C(dados.comando, dados.resposta);
									evento.setRetorno(dados.retorno);
									evento.setResposta(dados.resposta);
									break;
								}
								case enviarComando_FS_R: {
									evento.setTipoComando(ActionEventDarumaMobile.TipoComando.enviarComando_FS_R_Async);
									dados.retorno = darumaMobile.enviarComando_FS_R(dados.comando, dados.resposta);
									evento.setRetorno(dados.retorno);
									evento.setResposta(dados.resposta);
									break;
								}
								case confParametros: {
									evento.setTipoComando(ActionEventDarumaMobile.TipoComando.confParametrosAsync);
									dados.retorno = darumaMobile.confParametros(dados.comando);
									evento.setRetorno(dados.retorno);
									break;
								}
								case finalize: {
									executa = false;
								}
							default:
								break;
							} // switch
							
							evento.setStatus(true);
						} catch (Exception e) {
							evento.setResposta(new char[1024]);
							evento.setRetorno(-1);
							evento.setRetornoParametro("");
							evento.setRetornoParametros(new String[512]);
							evento.setStatus(false);
							evento.setDarumaError(e.getMessage());
						} // catch dos metodos da DM
						
						//se for false, não dá publish progress
						if(executa)
							this.publishProgress(evento);

					} // while
					
					Log.d(TAG, "Fim da Execução");
					
				} catch (Exception e) {
					this.publishProgress(new ActionEventDarumaMobile(false));
				}
			} catch (Exception e) {
				this.publishProgress(new ActionEventDarumaMobile(false));
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(ActionEventDarumaMobile... params) {
			listener.callback((ActionEventDarumaMobile)params[0]);			
		}
		
	}
	
	enum TipoComando {
		inicializar,
		iniciarComunicacao,
		fecharComunicacao,
		enviarComando,
		respostaComando,
		enviarResponderComando,
		retornarParametros,
		retornarParametroFramework,
		retornarParametroComunicacao,
		enviarComando_FS,
		enviarComando_FS_F,
		enviarComando_FS_R,
		enviarComando_FS_C,
		confParametros, finalize
	};
	
	class Dados {
		
		
		/**
		 * 	Qual é o comando.
		 */
		public TipoComando tipoMetodo;
		
		/**
		 * 	O valor a ser enviado pelo comando.
		 */
		public String comando = "";
		
		/**
		 * 	O valor a ser enviado pelo comando.
		 */
		public String[] retornoParametros = new String[512];
		
		/**
		 * 	Valor de suporte a ser enviado para a impressora.
		 */
		public String tipoComando = "";
		
		/**
		 * 	Variável de resposta da impressora.
		 */
		public char[] resposta = new char[1024];
		
		/**
		 * 	0 conseguiu enviar, -1 não conseguiu.
		 */
		public int retorno = 0;
				
		/**
		 * 	Fecha ou não a conexão.
		 */
		public boolean fechaConexao = false;
	}
}

