docker build -t testrunner .
docker run --rm -it testrunner
pause
docker run --rm -it -e SCALAWEBTEST_BASE_URI=https://www.unic.com testrunner
