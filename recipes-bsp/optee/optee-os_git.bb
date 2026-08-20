DESCRIPTION = "OP-TEE OS"

LICENSE = "BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=c1f21c4f72f372ef38a5a4aee55ec173 \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit deploy python3native

PV = "4.3.0+renesas+git${SRCPV}"

BRANCH = "rcar-gen4_4.3.0_s4+v4x"
SRCREV = "88f686737727df8e076e2dd84ab025b04d4469f4"

SRC_URI = " \
    git://github.com/renesas-rcar/optee_os.git;branch=${BRANCH};protocol=https \
"

COMPATIBLE_MACHINE = "sparrow-hawk"
PLATFORM = "rcar_gen4"

DEPENDS = "python3-cryptography-native python3-pyelftools-native"

export CROSS_COMPILE64="${TARGET_PREFIX}"

# Let the Makefile handle setting up the flags as it is a standalone application
#LD[unexport] = "1"
LDFLAGS[unexport] = "1"
libdir[unexport] = "1"

S = "${WORKDIR}/git"
EXTRA_OEMAKE = "-e MAKEFLAGS="

SOC:r8a779g0 = "V4H"
SOC:r8a779h0 = "V4M"
# Sparrow Hawk uses r8a779g3 silicon (V4H variant)
SOC:r8a779g3 = "V4H"

do_compile() {
    export CRYPTOGRAPHY_OPENSSL_NO_LEGACY=1

    if [ "${RGID_ON}" = "1" ]; then
        oe_runmake PLATFORM=${PLATFORM} LSI=${SOC} CFG_ARM64_core=y CFG_RCAR_RGID_ENABLE=y
    else
        oe_runmake PLATFORM=${PLATFORM} LSI=${SOC} CFG_ARM64_core=y
    fi
}

# do_install() nothing
do_install[noexec] = "1"

do_deploy() {
    # Create deploy folder
    install -d ${DEPLOYDIR}

    # Copy TEE OS to deploy folder
    install -m 0644 ${S}/out/arm-plat-${PLATFORM}/core/tee.elf ${DEPLOYDIR}/tee-${MACHINE}.elf
    install -m 0644 ${S}/out/arm-plat-${PLATFORM}/core/tee-raw.bin ${DEPLOYDIR}/tee-${MACHINE}.bin
    install -m 0644 ${S}/out/arm-plat-${PLATFORM}/core/tee.srec ${DEPLOYDIR}/tee-${MACHINE}.srec
}

addtask deploy before do_build after do_compile
