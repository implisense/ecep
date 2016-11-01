# ECEP - European Company Explorer Platform

The open data project by Implisense funded by ODINE. With ECEP you are able to search for any arbitrary company properties on company websites in Europe. Analyze and compare the latest company trends in different regions and industries. The beta version is now available for United Kingdom.

You can test it online at https://ecep.implisense.com/. There we have an installation, which is filled with company and web data and ready to answer your queries.

## Installation

You can also set up your own installation of our software. It needs a running [Elasticsearch](https://github.com/elastic/elasticsearch) installation. If you don't already have one, you can simply set it up and run it like that:

```shell
wget https://download.elastic.co/elasticsearch/release/org/elasticsearch/distribution/zip/elasticsearch/2.3.4/elasticsearch-2.3.4.zip
unzip elasticsearch-2.3.4.zip
./elasticsearch-2.3.4/bin/elasticsearch
```

To get the ECEP software, just clone this repository or download a snapshot as a zip file and unzip it:

```shell
wget https://github.com/implisense/ecep/archive/master.zip && unzip master.zip
```

This creates the directory ```ecep-master``` with the project files. For support of geographical data simply download http://www.doogal.co.uk/files/postcodes.zip and place it into the ```api``` directory:

```shell
cd ecep-master/api
wget http://www.doogal.co.uk/files/postcodes.zip
```

## License

Copyright 2016 Implisense GmbH

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## About

ECEP is maintained by [Implisense](http://implisense.com/) and funded by [ODINE](https://opendataincubator.eu/).

<img src="https://github.com/Dalphi/dalphi/blob/master/app/assets/images/implisense-logo.png" title="Implisense" alt="Implisense" width="200">

The Implisense GmbH is a technology company with headquarters in Berlin. The company is one of the technologically leading suppliers of sales intelligence for B2B. We love open source software and are [hiring](http://implisense.com/en/jobs/)!

<img src="https://ecep.implisense.com/img/odine-logo.png" title="ODINE" alt="ODINE" width="200">

The Open Data Incubator for Europe (ODINE) is a 6-month incubator for open data entrepreneurs across Europe.

