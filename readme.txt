------------------------------------------------------------------------
minikube start --cpus 4 --memory 4096
------------------------------------------------------------------------
kubectl create namespace kafka
kubectl create -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
kubectl get pod -n kafka --watch
kubectl apply -f https://strimzi.io/examples/latest/kafka/kafka-persistent-single.yaml -n kafka
kubectl wait kafka/my-cluster --for=condition=Ready --timeout=300s -n kafka

kubectl apply -f ~/GIT/kafka-service-a/k8s/card_transactions.yaml
kubectl apply -f ~/GIT/kafka-service-a/k8s/mobile_bank_logins.yaml
kubectl apply -f ~/GIT/kafka-service-a/k8s/trigger_messages.yaml
kubectl -n kafka get kafkatopics.kafka.strimzi.io

# Добавление доп. брокеров
kubectl get kafka my-cluster -n kafka -o yaml > ~/GIT/kafka-service-a/k8s/my-cluster-kafka.yaml
  kafka:
    replicas: 3
    metricsConfig:
      type: jmxPrometheusExporter
      valueFrom:
        configMapKeyRef:
          name: kafka-metrics-config
          key: kafka-metrics-config.yaml
  zookeeper:
    replicas: 1
    metricsConfig:
      type: jmxPrometheusExporter
      valueFrom:
        configMapKeyRef:
          name: zookeeper-metrics-config
          key: zookeeper-metrics-config.yaml

kubectl apply -f ~/GIT/kafka-service-a/k8s/my-cluster-kafka.yaml -n kafka
kubectl get pods -n kafka
------------------------------------------------------------------------
mvn clean package -DskipTests
eval $(minikube docker-env)
docker build -t kafka-service-a:0.0.1 .

kubectl apply -f ~/GIT/kafka-service-a/k8s/kafka-service-a.yaml
kubectl apply -f ~/GIT/kafka-service-a/k8s/a-service.yaml
kubectl port-forward svc/kafka-service-a 8080:8080 -n kafka

# тест метрик
kubectl port-forward svc/kafka-service-a 8080:8080 -n kafka
curl http://localhost:8080/actuator/prometheus
------------------------------------------------------------------------
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update

helm install prometheus prometheus-community/prometheus --namespace monitoring --create-namespace
helm install grafana grafana/grafana --namespace monitoring

kubectl apply -f ~/GIT/kafka-service-a/k8s/kafka-metrics-config.yaml
kubectl apply -f ~/GIT/kafka-service-a/k8s/zookeeper-metrics-config.yaml

helm upgrade --install prometheus prometheus-community/prometheus \
  --namespace monitoring \
  -f ~/GIT/kafka-service-a/k8s/prometheus_values.yaml

helm get values prometheus -n monitoring

# метрики от кафки:
kubectl port-forward my-cluster-kafka-0 9404:9404 -n kafka
curl http://localhost:9404/metrics
------------------------------------------------------------------------
------------------------------------------------------------------------
------------------------------------------------------------------------
------------------------------------------------------------------------
------------------------------------------------------------------------


