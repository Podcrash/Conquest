./gradlew jar
cp -vr ./build/libs/* ./server/plugins
cd ./server
./start.sh