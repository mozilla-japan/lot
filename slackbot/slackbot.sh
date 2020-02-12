#!/bin/bash -eu

CURDIR=$(cd $(dirname ${0}) && pwd)
PYTHON_PATH=$(cd $(dirname ${PYTHON_EXE}) && pwd)

(cd l10n/trunk/en-US && hg pull)
(cd l10n/trunk/en-US && hg update -C)
(cd src/trunk && git pull origin master)

${PYTHON_PATH}/compare-locales --json /tmp/compare-locales.json l10n/trunk/en-US/_configs/browser.toml src/trunk ja
${PYTHON_EXE} ${CURDIR}/slackbot.py
