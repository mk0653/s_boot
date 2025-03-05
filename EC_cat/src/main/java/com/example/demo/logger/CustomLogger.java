package com.example.demo.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

// @RequiredArgsConstructorを削除 - 手動でコンストラクタを定義するため
public class CustomLogger {
    private final Logger logger;

    // Lombokの@CustomLogアノテーションで使用される重要なコンストラクタ
    public CustomLogger(Logger logger) {
        this.logger = logger;
    }

    // スタックトレースを使用したコンストラクタ（手動で使用する場合用）
    public CustomLogger() {
        // スタックトレースから呼び出し元のクラス名を取得
        String className = Thread.currentThread().getStackTrace()[2].getClassName();
        this.logger = LoggerFactory.getLogger(className);
    }

    // クラスを指定するコンストラクタ
    public CustomLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    // 名前を指定するコンストラクタ
    public CustomLogger(String name) {
        this.logger = LoggerFactory.getLogger(name);
    }

    // 標準ログメソッド
    public void trace(String message) {
        logger.trace(message);
    }

    public void debug(String message) {
        logger.debug(message);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void error(String message) {
        logger.error(message);
    }

    // スタックトレース付きエラーログ
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    // フォーマット付きログメソッド
    public void info(String format, Object... arguments) {
        logger.info(format, arguments);
    }

    public void debug(String format, Object... arguments) {
        logger.debug(format, arguments);
    }

    public void error(String format, Object... arguments) {
        logger.error(format, arguments);
    }

    // マーカー付きログメソッド
    public void info(Marker marker, String message) {
        logger.info(marker, message);
    }

    // ロガーの存在確認
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }
}