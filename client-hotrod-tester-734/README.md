hotrod-tester
============


## 0. Use the following environment variables


| Variable name | Definition | Example | 
|---|---|---|
| DATAGRID_HOST | Host where the RHDG server is listening | 11222 | 
| DATAGRID_PORT | Port where the RHDG server is listening | 11222 |


## 1. Create the OCP application

```bash
## Configuration
export app_name=<app_name>
export namespace=<namespace>
export git_repo=<git_repo>

## Create application from template 
oc process -f ocp-template.yml -p APPLICATION_NAME=$app_name -p APPLICATION_NAMESPACE=$namespace -p GIT_REPOSITORY=$git_repo | oc apply -n $namespace -f -

## Expose route
oc expose svc $app_name -n $namespace

## Create configmap
oc create configmap ${app_name}-file-config --from-file=./src/main/resources/application.properties

## Start new build and deployment
oc start-build --follow $app_name -n $namespace
oc rollout latest $app_name -n $namespace


## Set environment variables
oc set env dc $app_name DATAGRID_HOST=rhdg73-4-server DATAGRID_PORT=11222 -n $namespace
oc set env dc $app_name --list
```


## 2. Alternative usage

```bash
#oc new-app --name hotrod-tester openshift/redhat-openjdk18-openshift~https://github.com/drhelius/hotrod-tester.git

```


## 3. Usage

Set your cluster url:
```bash
export cluster_url=<cluster_url>
export cache_name=default
```

```bash
curl "http://hotrod-tester-73-4-rhdg.${cluster_url}/api/cache/${cache_name}/put/?entries=100000&minkey=0"
```

Usage examples:
```bash
# PUT ASYNC
curl "http://hotrod-tester-73-4-rhdg.${cluster_url}/api/cache/${cache_name}/put/?entries=100000&minkey=0&async=true"
curl "http://hotrod-tester-73-4-rhdg.${cluster_url}/api/cache/${cache_name}/put/?entries=100000&minkey=100000&async=true"

# GET ASYNC
curl "http://hotrod-tester-73-4-rhdg.${cluster_url}/api/cache/${cache_name}/get/?entries=100000&minkey=0&async=true"

# PUT SYNC
curl "http://hotrod-tester-73-4-rhdg.${cluster_url}/api/cache/${cache_name}/put/?entries=100000&minkey=0"
curl "http://hotrod-tester-73-4-rhdg.${cluster_url}/api/cache/${cache_name}/put/?entries=100000&minkey=100000"
curl "http://hotrod-tester-73-4-rhdg.${cluster_url}/api/cache/${cache_name}/put/?entries=100000&minkey=200000"
curl "http://hotrod-tester-73-4-rhdg.${cluster_url}/api/cache/${cache_name}/put/?entries=100000&minkey=300000"

# Test 10 entries
curl "http://hotrod-tester-73-4-rhdg.${cluster_url}/api/cache/${cache_name}/put/?entries=10&minkey=0"

# Obtain stats
curl "http://hotrod-tester-73-4-rhdg.${cluster_url}/api/cache/${cache_name}/stats"

# Get value of 1 entry
curl "http://hotrod-tester-73-4-rhdg.${cluster_url}/api/cache/${cache_name}/get-single?key=0"
```
