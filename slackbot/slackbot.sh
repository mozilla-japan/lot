#!/bin/bash

CURDIR=$(cd $(dirname ${0})/.. && pwd)
GECKO_STRINGS=${CURDIR}/l10n/trunk/en-US
L10N_CENTRAL=${CURDIR}/l10n/firefox-l10n
MJ_GIT_REPO=${CURDIR}/src/trunk

if [ ! -f ${GECKO_STRINGS}/.git/config ] ; then
    mkdir -p ${GECKO_STRINGS}
    git clone https://github.com/mozilla-l10n/firefox-l10n-source.git ${GECKO_STRINGS}
fi
if [ ! -f ${L10N_CENTRAL}/.git/config ] ; then
    mkdir -p ${L10N_CENTRAL}
    git clone https://github.com/mozilla-l10n/firefox-l10n.git ${L10N_CENTRAL}
fi
if [ ! -f ${MJ_GIT_REPO}/.git/config ] ; then
    mkdir -p ${MJ_GIT_REPO}
    git clone https://github.com/mozilla-japan/gecko-l10n.git ${MJ_GIT_REPO}
fi

(cd ${GECKO_STRINGS} && git pull origin main)
(cd ${L10N_CENTRAL} && git pull origin main)
(cd ${MJ_GIT_REPO} && git pull origin master)

${PYTHON_EXE} ${CURDIR}/slackbot/slackbot.py ${GECKO_STRINGS}/_configs/browser.toml ${MJ_GIT_REPO} ${L10N_CENTRAL}
