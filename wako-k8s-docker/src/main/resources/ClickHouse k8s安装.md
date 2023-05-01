docker pull zookeeper:3.7
docker pull clickhouse/clickhouse-server:22.5
docker save -o zookeeper.tar zookeeper
docker save -o clickhouse-server.tar clickhouse/clickhouse-server:22.5

