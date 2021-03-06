#!/bin/bash

endpoint="build/dist"
cmd="/usr/bin/rsync -Pvaztu"

if  [[ $* == *--lib* ]] ;
then
    echo -e "Managing libs\n"
else
    cmd="${cmd} --exclude '${endpoint}/lib/'"
fi

echo Cmd: ${cmd}

cssgate="cssgate.insttech.washington.edu:~"
endpoint_cssgate="public_html/pi"

mkdir -p ${endpoint}

gradle build

echo -e "\n### Publishing Java byte code...\n"
${cmd} build/classes/main/* ${endpoint}/bin

echo -e "\n### Publishing php code...\n"
chmod -R 755 src/com/viveret/pilexa/pi/*
${cmd} src/com/viveret/pilexa/pi/php/ ${endpoint}/

echo -e "\n### Publishing configuration files...\n"
chmod -R 755 build/resources/main/*
${cmd} build/resources/main/ ${endpoint}/

echo -e "\n### Publishing helper scripts...\n"
${cmd} $(echo *.sh) $(echo *.md) ${endpoint}/

if  [[ $* == *--lib* ]] ;
then
    echo -e "\n### Publishing libraries...\n"
    gradle copyRuntimeLibs
    ${cmd} build/dep/ ${endpoint}/lib
fi

if [[ $* == *--zip* ]] ;
then
    echo -e "\n### Zipping distributables...\n"
    mkdir -p ${endpoint}-zip
    zip -r ${endpoint}-zip/pi.zip ${endpoint}
    tar -zcvf ${endpoint}-zip/pi.tgz ${endpoint}
fi

if  [[ $* == *--cssgate* ]] ;
then
    read -p "Enter UW NetID: " username
    # read -sp "Enter password: " password

    # expect="/usr/bin/expect"
    # expect_pre=""
    # expect_post="expect \"password:\" \n send \"@password\r\" \n expect eof\n"

    endpoint_local="${endpoint}"
    endpoint="${username}@${cssgate}/${endpoint_cssgate}"
    # expect=${expect/@password/$password}

    #${expect} << EOC
    #set timeout -1
    #spawn
    echo "${cmd} --delete ${endpoint_local}/ ${endpoint}"
    ${cmd} --delete ${endpoint_local}/ ${endpoint}
    #${expect_post}
    #EOC
fi

echo -e "\n### Done."

# rm $rsyncPassFile

