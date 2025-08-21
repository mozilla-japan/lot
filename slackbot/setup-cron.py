import os
import sys
import stat
import subprocess

print("checking for datetime.fromisoformat ...")
import datetime
datetime.datetime.fromisoformat("2011-11-04T00:05:23")
print("ok.")

print("checking for requests ...")
import requests
print("ok.")

print("checking for pyquery ...")
import pyquery.pyquery
print("ok.")

print("checking for compare-locales ...")
import compare_locales
print("ok.")

print("checking for git ...")
subprocess.run(["git", "--version"], check=True)
print("ok.")

PYTHON_EXE = sys.executable
SCRIPT_ROOT = os.path.dirname(os.path.abspath(sys.argv[0]))
L10NBOT_TOKEN = os.getenv("L10NBOT_TOKEN")

print("checking for python executable ...")
if not PYTHON_EXE:
    raise ValueError
print("ok.")

print("checking for script root ...")
if not SCRIPT_ROOT:
    raise ValueError
print("ok.")

print("checking for L10NBOT_TOKEN ...")
if not L10NBOT_TOKEN:
    raise ValueError
print("ok.")


#CRON = "/tmp/l10n-bot"
CRON = "/etc/cron.daily/l10n-bot"
with open(CRON, "w") as f:
    print("""#!/bin/bash -eu
export PYTHON_EXE=%s
export L10NBOT_TOKEN=%s
%s/slackbot.sh""" % (PYTHON_EXE, L10NBOT_TOKEN, SCRIPT_ROOT), file=f)
os.chmod(CRON, os.stat(CRON).st_mode | stat.S_IXUSR | stat.S_IXGRP | stat.S_IXGRP)
