#!/bin/bash

CURDIR=$(cd $(dirname ${0})/.. && pwd)
PYTHON_PATH=$(cd $(dirname ${PYTHON_EXE}) && pwd)
export PATH=${PATH}:${PYTHON_PATH}

GECKO_STRINGS=${CURDIR}/l10n/trunk/en-US
L10N_CENTRAL=${CURDIR}/l10n/trunk/ja
MJ_GIT_REPO=${CURDIR}/src/trunk

if [ ! -f ${GECKO_STRINGS}/.hg/hgrc ] ; then
    mkdir -p ${GECKO_STRINGS}
    hg clone https://hg.mozilla.org/l10n/gecko-strings/ ${GECKO_STRINGS}
fi
if [ ! -f ${L10N_CENTRAL}/.hg/hgrc ] ; then
    mkdir -p ${L10N_CENTRAL}
    hg clone https://hg.mozilla.org/l10n-central/ja/ ${L10N_CENTRAL}
fi
if [ ! -f ${MJ_GIT_REPO}/.git/config ] ; then
    mkdir -p ${MJ_GIT_REPO}
    git clone https://github.com/mozilla-japan/gecko-l10n.git ${MJ_GIT_REPO}
fi

(cd ${GECKO_STRINGS} && hg pull)
(cd ${GECKO_STRINGS} && hg update -C)
(cd ${L10N_CENTRAL} && hg pull)
(cd ${L10N_CENTRAL} && hg update -C)
(cd ${MJ_GIT_REPO} && git pull origin master)

${PYTHON_EXE} ${CURDIR}/slackbot/slackbot.py ${GECKO_STRINGS}/_configs/browser.toml ${MJ_GIT_REPO} ${L10N_CENTRAL}/..
