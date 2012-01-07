all: compile pack predebpack

compile:
	echo "build keyboard with ant"
	ant pre-build build
pack:
	echo "pack with ant into jar"
	ant pack-jar
clean:
	ant clean
predebpack:
	echo "prepare for debian packaging"
	./prepackaging.sh
