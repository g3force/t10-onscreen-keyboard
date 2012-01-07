#!/bin/sh

echo "update debian/t10-keyboard.install:"
#echo "`cat debian/t10-keyboard.install | grep -v .jar`" > debian/t10-keyboard.install
echo "debian/t10-keyboard.sh usr/share/t10-keyboard/
debian/t10-keyboard.desktop /usr/share/applications
debian/t10-keyboard_icon.png usr/share/t10-keyboard" > debian/t10-keyboard.install

version="`cat src/res/version`"
if [ -z "$version" ]; then
  echo "Error: Version empty"
  exit 1
fi
filename="`ls build | grep \"$version.jar\"`"
if [ -z "$filename" ]; then
  echo "Error: Filename empty"
  exit 1
fi
echo "build/$filename usr/share/t10-keyboard" >> debian/t10-keyboard.install
cat debian/t10-keyboard.install

echo
echo "update debian/t10-keyboard.sh:"
echo "#!/bin/sh" > debian/t10-keyboard.sh
echo "java -jar /usr/share/t10-keyboard/$filename \$@" >> debian/t10-keyboard.sh
cat debian/t10-keyboard.sh
