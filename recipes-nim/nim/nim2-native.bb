DESCRIPTION = "nim native compiler"
SECTION = "languages"
LICENSE = "CLOSED"

SRCREV = "devel"
SRC_URI = "git://github.com/nim-lang/Nim.git;protocol=https;branch=devel \
  file://build_all.sh \
"

DEPENDS += " git bash"

S = "${WORKDIR}/git"

FILES_${PN} = "${bindir}/* ${includedir}/* ${libdir}/*"

do_compile() {
#    export LDFLAGS=""
    export LD="$CC"
    #sed -i 's/build.sh/build.sh --cpu ${TARGET_ARCH}/g' build_all.sh
    bash build_all.sh
}

BBCLASSEXTEND="native"

SSTATE_DUPWHITELIST = "/"

do_install() {
    ./koch install ${D}${bindir}
} 


do_install2() {
    install -d ${D}${bindir}
    install -d ${D}${includedir}
    install -d ${D}${libdir}/nim
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

