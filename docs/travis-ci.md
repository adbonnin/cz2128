# Travis-CI

## Key Generation

Create and export keys :
```
gpg --gen-key
gpg --export-secret-keys > codesigning.asc
gpg --send-keys --keyserver keyserver.ubuntu.com <KEY ID>
```

Encrypt secrets with Travis :
```
travis encrypt OSSRH_PASSWORD='<sonatype password>' --add env.global
travis encrypt OSSRH_USERNAME='<sonatype username>' --add env.global
travis encrypt GPG_KEY_NAME='<gpg key name>' --add env.global
travis encrypt GPG_PASSPHRASE='<gpg passphrase>' --add env.global
travis encrypt-file codesigning.asc --add
```

source
- https://bpodgursky.com/2019/07/31/using-travisci-to-deploy-to-maven-central-via-git-tagging-aka-death-to-commit-clutter/
- https://github.com/wix-incubator/teamcity-client/blob/master/.travis.yml
- https://www.juliaaano.com/blog/2017/04/10/build-and-release-pipeline/
- https://stackoverflow.com/questions/38276762/travis-gpg-signing-failed-secret-key-not-available/38280618#38280618
