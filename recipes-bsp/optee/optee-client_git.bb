DESCRIPTION = "OP-TEE Client"
LICENSE = "BSD-2-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=69663ab153298557a59c67a60a743e5b"
PR = "r0"
PV = "4.3.0+renesas+git${SRCPV}"
BRANCH = "master"
SRC_URI = "git://github.com/OP-TEE/optee_client.git;branch=${BRANCH};protocol=https"
SRCREV = "a5b1ffcd26e328af0bbf18ab448a38ecd558e05c"

SRC_URI += " \
    file://optee.service \
    file://0001-tee-supplicant-Delete-the-sleep-time-when-writing-da.patch \
"

inherit python3native systemd
DEPENDS += "pkgconfig-native util-linux"
EXTRA_OEMAKE += "PKG_CONFIG=pkg-config"
SYSTEMD_SERVICE:${PN} = "optee.service"

COMPATIBLE_MACHINE = "(sparrow-hawk|generic-armv8-xt)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/git"

EXTRA_OECMAKE = " \
    -DRPMB_EMU=0 \
    -DBUILD_SHARED_LIBS=ON \
    -DCFG_TEE_FS_PARENT_PATH='${localstatedir}/lib/tee' \
    -DCFG_WERROR=0 \
"

do_install () {
    # Create destination directories
    install -d ${D}/${libdir}
    install -d ${D}/${bindir}
    install -d ${D}/${includedir}

    # Install shared and static libraries
    install -m 0644 ${S}/out/export/usr/lib/libteec.a ${D}/${libdir}/libteec.a
    install -m 0644 ${S}/out/export/usr/lib/libckteec.a ${D}/${libdir}/libckteec.a
    install -m 0644 ${S}/out/export/usr/lib/libseteec.a ${D}/${libdir}/libseteec.a
    install -m 0644 ${S}/out/export/usr/lib/libteeacl.a ${D}/${libdir}/libteeacl.a
    install -m 0755 ${S}/out/export/usr/lib/libteec.so.2.0.0 ${D}/${libdir}/libteec.so.2.0.0
    install -m 0755 ${S}/out/export/usr/lib/libckteec.so.0.1.0 ${D}/${libdir}/libckteec.so.0.1.0
    install -m 0755 ${S}/out/export/usr/lib/libseteec.so.0.1.0 ${D}/${libdir}/libseteec.so.0.1.0
    install -m 0755 ${S}/out/export/usr/lib/libteeacl.so.0.1.0 ${D}/${libdir}/libteeacl.so.0.1.0

    # Create symbolic links for shared libraries
    ln -sf libteec.so.2.0.0 ${D}/${libdir}/libteec.so.2.0
    ln -sf libteec.so.2.0 ${D}/${libdir}/libteec.so.2
    ln -sf libteec.so.2 ${D}/${libdir}/libteec.so
    ln -sf libckteec.so.0.1.0 ${D}/${libdir}/libckteec.so.0.1
    ln -sf libckteec.so.0.1 ${D}/${libdir}/libckteec.so.0
    ln -sf libckteec.so.0 ${D}/${libdir}/libckteec.so
    ln -sf libseteec.so.0.1.0 ${D}/${libdir}/libseteec.so.0.1
    ln -sf libseteec.so.0.1 ${D}/${libdir}/libseteec.so.0
    ln -sf libseteec.so.0 ${D}/${libdir}/libseteec.so
    ln -sf libteeacl.so.0.1.0 ${D}/${libdir}/libteeacl.so.0.1
    ln -sf libteeacl.so.0.1 ${D}/${libdir}/libteeacl.so.0
    ln -sf libteeacl.so.0 ${D}/${libdir}/libteeacl.so

    # Install header files
    install -m 0644 ${S}/out/export/usr/include/* ${D}/${includedir}

    # Install binary to bindir
    install -m 0755 ${S}/out/export/usr/sbin/tee-supplicant ${D}/${bindir}

    # Install systemd service configure file for OP-TEE client
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}/${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/optee.service ${D}/${systemd_system_unitdir}
    fi
}

RPROVIDES:${PN} += "optee-client"
