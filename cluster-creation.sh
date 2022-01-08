# see https://docs.aws.amazon.com/eks/latest/userguide/getting-started-eksctl.html

# setting up eksctl
brew tap weaveworks/tap
brew install weaveworks/tap/eksctl
eksctl create cluster \
  --name routing-cluster \
  --region eu-central-1 \
  --fargate
eksctl create nodegroup --cluster=routing-cluster
