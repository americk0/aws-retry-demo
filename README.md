# How to run

First, create an entry in your `~/.aws/credentials` for the AWS profile `test123` like this:

```
[test123]
aws_access_key_id=ABCD
aws_secret_access_key=ABCD
aws_session_token=ABCD
```

and replace the ABCD's with a real set of temporary access keys generated from AWS using sts:AssumeRole (this can be done via the aws cli using `aws sts assume-role ...`)

Next, Install the Maven cli (mvn). 

Then, in the same folder as the pom.xml file, run

```bash
mvn spring-boot:run
```

