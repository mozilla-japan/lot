#!/usr/bin/python3

import datetime
import os
import sys
import traceback

# pip install
import requests
import pyquery.pyquery
from compare_locales.paths import TOMLParser
from compare_locales.compare import compareProjects

PONTOON_URL = "https://pontoon.mozilla.org/ja/firefox/"


def calendarCheck():
    d = pyquery.pyquery.PyQuery(url=PONTOON_URL)
    deadline = datetime.datetime.strptime(d("#heading li.deadline time").text(), "%b %d, %Y")
    remain = deadline.date() - datetime.datetime.utcnow().date()
    return "次回締切まであと *%d 日* (%s)\n" % (remain.days, deadline.date())


def compare(browser_toml, l10n_base_dir):
    config_env = {"l10n_base": l10n_base_dir}
    configs = [TOMLParser().parse(browser_toml, env=config_env)]
    return compareProjects(configs, ["ja"], l10n_base_dir).toJSON()

def l10nStatus(browser_toml, mj_git_repo, l10n_central):
    missing         = compare(browser_toml,  mj_git_repo)["summary"]["ja"].get("missing", 0)
    missing_central = compare(browser_toml, l10n_central)["summary"]["ja"].get("missing", 0)

    if missing:
        message = "firefoxの翻訳状況: *%d missing*" % (missing)
    else :
        message = "firefoxの翻訳状況: Translated"
    if missing > missing_central:
        message += "\n_l10n-centralが先行:_ %d Translated" % (missing - missing_central)
    return message


def postSlack(token, text):
    data = {"token":token, "channel":"#l10n", "text":text}
    requests.post("https://slack.com/api/chat.postMessage", data=data)


if __name__ == "__main__":
    token = os.environ.get("L10NBOT_TOKEN")
    try:
        (browser_toml, mj_git_repo, l10n_central) = sys.argv[1:]
        text = calendarCheck() + l10nStatus(browser_toml, mj_git_repo, l10n_central)
    except:
        text = "```%s```" % traceback.format_exc()
    finally:
        if token:
            postSlack(token, text)
        else:
            print(text)
