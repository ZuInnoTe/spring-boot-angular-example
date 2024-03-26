# Introduction
We cannot give a full-overview of all possible things to consider for operating an application. See this as an introduction to look for more.

# Environments
You should have different environments for your application so that you do not impact your users when developing new features of the application or when doing load testing. As a minimum set of environments you can think of:
* Local development - this is the environment used by the developers - often a local machine - to develop the application
* Development - this is the environment where multiple developer changes are brought together and deployed to see that they can work together
* Test - here developers and users do functional tests, integration tests, performance tests etc.
* Production - here the application is provided to the end users to support them in their business tasks

# Secrets Management
You should manage secrets (e.g. database passwords) in dedicated secret vaults and fetch them during runtime.

NEVER put secrets in container images or application packages - this is insecure.

Secret vaults often also allow rotating  (changing) regularly secrets for an improved security.

An example for a secret vault is [AWS Secrets Manager](https://docs.aws.amazon.com/secretsmanager/) or [HashiCorp Vault](https://developer.hashicorp.com/vault/docs).

The best way would be though to avoid using secrets at all in your application. Cloud provider offer for this short-living tokens (e.g. AWS IAM Execution Roles or Azure Managed Identities). Their advantage is that you do not need to manage secrets anymore at all.

Standards are emerging to enable this [cross-cloud](https://zuinnote.eu/blog/?p=2273).

# Security Hardening
You should harden your application and the environment where it runs. The [CIS Security Benchmarks](https://www.cisecurity.org/cis-benchmarks) provide some information how you can do this.

Additionally, for containers and infrastructure-as-code, you can use static analyis tools, such as [terrascan](https://github.com/tenable/terrascan) and/or [checkov](https://github.com/bridgecrewio/checkov) to get insights how to improve your environment.

# Upgrading/Patching
You should regularly patch/update third party dependencies of your application and the underlying infrastructure (e.g. container image and VMs). These patches should happen frequently (e.g. weekly) and they must be also deployable ad-hoc (e.g. in case of zero-day exploits).

One important thing one needs to consider is to have a very low (or non-existing) down-time of the application, e.g. by having load-balancer redirecting traffic to multiple instances of your application or only redirecting traffic to a newly deployed instance of your application if health-checks have been passed.

You should be able to restore infrastructures, configurations, binaries from a versioned repository so that you can easily recover your infrastructure in case of failures. Obviously you should test beforehand in another non-production environment that this works as expected.

# Backups/Restore
Usually applications process crucial data in databases, object stores, filesystems etc. Loosing data often means loosing lifes (e.g. in healthcare domain), money, jobs etc. 

Hence you should back the data up in a save place outside your application environment.

You should regularly test if the data can successfully restored using restore test where you also need to test if after a restore your application still works.

Important [indicators](https://en.wikipedia.org/wiki/IT_disaster_recovery) are:
* Recovery Point Objective (RPO)
* Recovery Time Objective (RTO)

# Monitoring
This depends on your environment and the existing monitoring application that you have. 

Within this monitoring application you should be able to configure rules that fire on errors appearing in logs and automatically triggering actions (e.g. on database error restart the database).

Of course also humans could be notified via e-mail.

For example, on AWS you can choose [AWS Fargate](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/AWS_Fargate.html) to run a container and it automatically integrates the logs into [AWS Cloudwatch](https://docs.aws.amazon.com/cloudwatch/). There you could use Cloudwatch Metric/Alerts to trigger on error automated actions. AWS Fargate can check the health endpoint automatically exposed by Spring Boot Actuator. If it detects it is unhealthy it will automatically request additional healthy container instances to serve end users requests.

Of course you can use any other monitoring stack to do essentially the same.

