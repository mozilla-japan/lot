#!/bin/bash

CURDIR=$(cd $(dirname ${0})/.. && pwd)
PYTHON_PATH=$(cd $(dirname ${PYTHON_EXE}) && pwd)

if [ ! -d ${CURDIR}/l10n/trunk/en-US ] ; then
    mkdir -p ${CURDIR}/l10n/trunk/en-US
    hg clone https://hg.mozilla.org/l10n/gecko-strings/ ${CURDIR}/l10n/trunk/en-US
fi
if [ ! -d ${CURDIR}/src/trunk ] ; then
    mkdir -p ${CURDIR}/src/trunk
    git clone https://github.com/mozilla-japan/gecko-l10n.git ${CURDIR}/src/trunk
fi

(cd ${CURDIR}/l10n/trunk/en-US && hg pull)
(cd ${CURDIR}/l10n/trunk/en-US && hg update -C)
(cd ${CURDIR}/src/trunk && git pull origin master)

${PYTHON_PATH}/compare-locales --json /tmp/compare-locales.json ${CURDIR}/l10n/trunk/en-US/_configs/browser.toml ${CURDIR}/src/trunk ja > /dev/null
${PYTHON_EXE} ${CURDIR}/slackbot/slackbot.py
