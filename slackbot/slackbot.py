#!/usr/bin/python3

import datetime
import os
import traceback
import json

# pip install
import requests
import pyquery.pyquery

PONTOON_URL = "https://pontoon.mozilla.org/ja/firefox/"
NOW = datetime.datetime.utcnow()
NOW_STR = NOW.isoformat()


def calendarCheck():
    d = pyquery.pyquery.PyQuery(url=PONTOON_URL)
    deadline = datetime.datetime.strptime(d("#heading li.deadline time").text(), "%b %d, %Y")
    remain = deadline.date() - NOW.date()
    return "次回締切まであと *%d 日*\n" % (remain.days)


def l10nStatus():
    with open("/tmp/compare-locales.json") as f:
        result = json.load(f)[0]["summary"]["ja"]
    missing = result.get("missing")

    if missing:
        return "firefoxの翻訳状況: *%d missing*" % (missing)
    else :
        return "firefoxの翻訳状況: Translated"


def postSlack(token, text):
    data = {"token":token, "channel":"#l10n", "text":text}
    requests.post("https://slack.com/api/chat.postMessage", data=data)


if __name__ == "__main__":
    token = os.environ.get("L10NBOT_TOKEN")
    try:
        text = calendarCheck() + l10nStatus()
    except:
        text = "```%s```" % traceback.format_exc()
    finally:
        if token:
            postSlack(token, text)
        else:
            print(text)
