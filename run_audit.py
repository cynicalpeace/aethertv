#!/usr/bin/env python3
import pexpect
import sys
import os
import time

os.chdir('/home/hero/acestream')
os.environ['CLAUDE_CODE_EXPERIMENTAL_AGENT_TEAMS'] = '1'

prompt = '''You are leading a SEVENTH deep code audit of this Android TV app. Six previous audits found and fixed 17 critical and 36 high priority issues.

Read AUDIT_REPORT.md first to see all previous findings.

## Your Mission
Create an agent team with 5 specialists to audit this codebase in parallel.

## Team Structure
Spawn these 5 teammates:

1. **Security Auditor** - Focus: hardcoded secrets, SQL injection, path traversal, unsafe deserialization, HTTP vs HTTPS, input validation
   
2. **Concurrency Specialist** - Focus: race conditions, deadlocks, atomicity violations, wrong dispatchers, coroutine scope leaks, Flow collection issues

3. **Resource/Lifecycle Analyst** - Focus: memory leaks, context leaks, unclosed streams, listener cleanup, lifecycle-aware components, ViewModel cleanup

4. **Compose/UI Expert** - Focus: recomposition storms, remember keys, derivedStateOf usage, LaunchedEffect cleanup, state hoisting, TV focus/accessibility

5. **Devil's Advocate** - Challenge findings, find what others missed, verify previous fixes work

## Process
1. Have all 5 teammates audit in parallel
2. Have them challenge each other's findings  
3. Update AUDIT_REPORT.md with '## Seventh Audit (Agent Team)' section
4. Fix ALL Critical and High priority issues
5. Build with: JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 ./gradlew assembleDebug

When finished, run: openclaw gateway wake --text 'Seventh audit complete' --mode now'''

# Escape quotes in prompt
prompt_escaped = prompt.replace('"', '\\"')

child = pexpect.spawn(f'claude --permission-mode bypassPermissions "{prompt_escaped}"', 
                      encoding='utf-8', timeout=7200, dimensions=(50, 200))
child.logfile = sys.stdout

# Wait for the menu to appear
try:
    idx = child.expect(['Yes, I accept', pexpect.TIMEOUT, pexpect.EOF], timeout=30)
    if idx == 0:
        print("\n>>> Found menu, sending Down + Enter <<<\n", file=sys.stderr)
        time.sleep(0.5)
        child.send('\x1b[B')  # Down arrow (ESC [ B)
        time.sleep(0.3)
        child.send('\r')  # Enter
        time.sleep(0.5)
except Exception as e:
    print(f"\n>>> Exception: {e} <<<\n", file=sys.stderr)

# Now wait for completion
try:
    child.expect(pexpect.EOF, timeout=7200)
except:
    pass

print("\n>>> Script completed <<<\n")
