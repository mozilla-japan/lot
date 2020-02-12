#!/usr/bin/python3

import datetime
import os
import traceback
import json

# pip install
import ics
import requests

REKEASE_WIKI = "https://wiki.mozilla.org/Release_Management/Calendar"
CALENDAR_PAGE = "https://www.google.com/calendar/embed?src=bW96aWxsYS5jb21fZGJxODRhbnI5aTh0Y25taGFiYXRzdHY1Y29AZ3JvdXAuY2FsZW5kYXIuZ29vZ2xlLmNvbQ"
ICS_URL = "https://calendar.google.com/calendar/ical/mozilla.com_dbq84anr9i8tcnmhabatstv5co%40group.calendar.google.com/public/basic.ics"
NOW = datetime.datetime.utcnow()
NOW_STR = NOW.isoformat()


def getLocaleCheck(event):
    return NOW_STR < str(event.begin) and "Final locale check" in event.name

def getSignoff(event):
    return NOW_STR < str(event.begin) and ("pre-release signoff" in event.name or "pre-release sign-off" in event.name)

def getReleased(event):
    return NOW_STR < str(event.begin) and "released" in event.name and not "RC" in event.name

def formatLimit(event):
    remain = datetime.datetime.fromisoformat(str(event.begin)).date() - NOW.date()
    return "%s まであと *%d 日*" % (event.name, remain.days)

def calendarCheck():
    cal = ics.Calendar(requests.get(ICS_URL).text)
    localeCheck = sorted(filter(getLocaleCheck, cal.events))
    signoff     = sorted(filter(getSignoff,     cal.events))
    released    = sorted(filter(getReleased,    cal.events))

    error = False
    text = ""
    if len(localeCheck):
        text += formatLimit(localeCheck[0]) + "\n"
    else:
        error = True
        text += "チェック日不明\n"

    if len(signoff):
        text += formatLimit(signoff[0]) + "\n"
    else:
        error = True
        text += "サインオフ期限不明\n"

    if len(released):
        text += formatLimit(released[0]) + "\n"
    else:
        error = True
        text += "リリース日不明\n"

    if error:
        text += "カレンダー %s\n" % (CALENDAR_PAGE)
    return text


def l10nStatus():
    with open("/tmp/compare-locales.json") as f:
        result = json.load(f)[0]["summary"]["ja"]
    missing = result["missing"]

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
