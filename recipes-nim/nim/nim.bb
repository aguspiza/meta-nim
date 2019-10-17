DESCRIPTION = "nim compiler"
SECTION = "languages"
LICENSE = "CLOSED"

SRCREV = "v1.0.0"
SRC_URI = "git://github.com/nim-lang/Nim.git;protocol=https;branch=devel \
  file://build_all.sh \
"

DEPENDS += " git bash nim-native"
BBCLASSEXTEND="native"

S = "${WORKDIR}/git"

FILES_${PN} = "${bindir}/* ${includedir}/* ${libdir}/*"

do_compile() {
#    export LDFLAGS=""
    export LD="$CC"
    sed -i 's/build.sh/build.sh --cpu ${TARGET_ARCH}/g' build_all.sh
    if [ "${PN}" = "nim-native" ]; then
      bash build_all.sh
    else
      echo 'arm.linux.gcc.exe = "arm-poky-linux-gnueabi-gcc"' >> compiler/nim.cfg
      ${STAGING_BINDIR_NATIVE}/nim/bin/nim dump
      ${STAGING_BINDIR_NATIVE}/nim/bin/nim c --cpu:${TARGET_ARCH} --os:linux -d:release --compileOnly --genScript --nimcache:nimcache compiler/nim.nim
      cd nimcache
      sed -i 's/gcc/$CC/g' compile_nim.sh
      chmod +x ./compile_nim.sh
      ./compile_nim.sh
      #koch tools
    fi
}

SSTATE_DUPWHITELIST = "/"
INSANE_SKIP_${PN} = "ldflags"

do_install() {
    if [ "${PN}" = "nim-native" ]; then
        ./koch install ${D}${bindir}
        install -D -m 755 ./koch ${D}${bindir}/koch
        install -d ${D}${libdir}/nim
        install -d lib ${D}${libdir}/nim/
    else
        install -d ${D}${libdir}/nim
        install -d ${D}${includedir}
        install -d lib ${D}${libdir}/nim/
        install -D -m 755 ${B}/nimcache/nim ${D}${bindir}/nim
        install -m 755 ${S}/lib/*.h ${D}${includedir}/

        #    install -m 755 ${S}/lib/**/* ${D}${libdir}/nim/
        (
            cd ${S}/lib
            for dir in $(find . -type d);do
                install -d ${D}${libdir}/nim/$dir
            done
            for file in $(find . -type f);do
                install -m 755 "$file" ${D}${libdir}/nim/$file
            done
        )
    fi
} 


do_install2() {
    install -d ${D}${bindir}
    install -d ${D}${includedir}
    install -d ${D}${libdir}/nim
    install -d lib ${D}${libdir}/nim/
    install -m 755 ${S}/bin/* ${D}${bindir}/
    install -m 755 ${S}/lib/*.h ${D}${includedir}/

#    install -m 755 ${S}/lib/**/* ${D}${libdir}/nim/
    (
    cd ${S}/lib
    for dir in $(find . -type d);do
        install -d ${D}${libdir}/nim/$dir
    done
    for file in $(find . -type f);do
        install -m 755 "$file" ${D}${libdir}/nim/$file
    done
    )
}

