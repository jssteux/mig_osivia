repos=$HOME/Dev/portal-modules-repos/common/trunk-SNAPSHOT/lib
thirdparty=$HOME/Dev/jboss-portal-2.7/thirdparty/jboss-portal/modules/common/lib/

echo "Copies current version of web libraries either to local repository copy or Portal thirdparty to test or release purpose"
echo "Usage: '$0' to release to Portal thirdparty, '$0 repos' to release to repository local copy"
echo "Set 'repos' variable to the snapshot lib directory for the common module of your local repository copy"
echo "Set 'thirdparty' variable to the lib directory for the common module of your local JBoss Portal 2.7 thirparty directory"
echo ""
echo "repos currently set at: $repos"
echo "thirdparty currently set at: $thirdparty"
echo ""

if [[ $1 == "repos" ]]; then
  loc=$repos
  echo "Releasing to repository. Don't forget to update component-info.xml with revision number."
elif [[ $1 == "maven" ]]; then
  echo "Releasing to Maven repository. You will need to have your credentials in settings.xml."
  mvn deploy -Djboss.repository.root=https://snapshots.jboss.org/maven2/
  exit 0
elif [[ $1 == "usage" ]]; then
  echo "Usage shown, nothing was done"
  exit 0
else
  loc=$thirdparty
  echo "Releasing to Portal thirdparty"
fi


cp web/target/web-web-1.2.0-SNAPSHOT.jar $loc/portal-web-lib.jar