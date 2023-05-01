kubectl get ns
kubectl get pods --all-namespaces
kubectl get pods --namespace=infra
kubectl get deploy --namespace=infra
kubectl describe pod nfs-client-provisioner-9c99d8fc9-tjg4l --namespace=infra ##排查
