
typeperf "\Processor(_Total)\% Privileged Time" "\Processor(_Total)\% User Time" "\Processor(_Total)\% Processor Time" "\System\Processor Queue Length" "\Memory\Available MBytes" "\Memory\Pages/sec" -f csv -o output.csv
typeperf "\Network Interface(*)\Bytes Total/sec" "\Network Interface(*)\Current Bandwidth/sec" -f csv -o output.csv