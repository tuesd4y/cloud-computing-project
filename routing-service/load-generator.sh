#!/bin/bash

kubectl run -i --tty load-generator -n routing-service --rm --image=busybox --restart=Never -- /bin/sh -c "while sleep 0.001; do wget -q -O- 'http://routing-service-berlin-car-entrypoint:5000/route/v1/driving/13.388860,52.517037;13.385983,52.496891?steps=true'; done"
