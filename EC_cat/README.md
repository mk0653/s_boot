# EC_cat - 注文管理アプリ

Spring Boot製の注文管理REST APIアプリケーション

## 必要環境

- Java 17以上
- Maven（Maven Wrapper同梱）

## ビルド・起動

```bash
# EC_catフォルダに移動
cd EC_cat

# ビルドのみ
./mvnw.cmd clean package

# 起動（ビルド含む）
./mvnw.cmd spring-boot:run

# JARファイルで起動（ビルド後）
java -jar target/EcCat-0.0.1-SNAPSHOT.jar
```

## API エンドポイント

| メソッド | URL | 説明 |
|----------|-----|------|
| GET | /orders | 注文一覧を取得 |
| POST | /orders | 注文を作成 |
| PUT | /orders/{id}/status | 注文ステータス更新 |
| DELETE | /orders/{id} | 注文を削除 |

## アクセス先

- アプリ: http://localhost:8088/orders
- H2コンソール: http://localhost:8088/h2-console

## H2データベース接続情報

- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: （空欄）
