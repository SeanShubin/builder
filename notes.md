### event
- ready
- found local
- found github
- ready to clone
- ready to build
- clone success
- clone fail
- build success
- build fail
- finished

### action
- find local
- find github
- clone
- build
- shutdown

### state (events waiting on)
- initial
    - ready
        - find local
        - find github
- found local not github
    - find github
- found github not local
    - find local
- processing
    - clone success
    - clone fail
    - build success
    - build fail
- all done
