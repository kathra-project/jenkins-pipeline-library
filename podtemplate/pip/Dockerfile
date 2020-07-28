FROM python:3.6-alpine
RUN apk add --no-cache curl gcc libc-dev
RUN pip install --upgrade pip setuptools wheel twine==2.0.0
RUN pip install nose coverage pytest-cov pylint nosexcover
CMD ["/bin/sh"]
