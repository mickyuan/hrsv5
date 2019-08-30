package hrds.exception;

import fd.ng.core.exception.BusinessSystemException;

public class AppSystemException extends BusinessSystemException {
	public AppSystemException(final String msg) { super(msg);
	}
	public AppSystemException(final Throwable cause) {
		super(cause);
	}

	public AppSystemException(final String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 *
	 * @param canbeGettedMessage 反馈前端的消息，通过 getMessage() 获得
	 * @param loggedMessage 仅用于打印到日志中的消息
	 * @param cause 真正发生的异常
	 */
	public AppSystemException(String canbeGettedMessage, String loggedMessage, Throwable cause) {
		super(canbeGettedMessage, loggedMessage, cause);
	}
}
