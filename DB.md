## Configuração do Banco de Dados POSTGRES

```bash
docker run --name alien_db \                     ─╯
           -e POSTGRES_USER=postgres \
           -e POSTGRES_PASSWORD=123 \
           -e POSTGRES_DB=alien \
           -p 5432:5432 \
           -v pgdata:/var/lib/postgresql/data \
           -d postgres:14
```

