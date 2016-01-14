import urllib.request
import re


inputURL = input('Enter the base URL: ')

if len(inputURL) == 0:
    print("No URL provided.")
    exit(1)

if not inputURL.startswith('http'):
    inputURL = 'http://' + inputURL

if re.search('\\.(html?|php)$', inputURL) is not None:
    index = inputURL.rfind('/')
    baseURL = inputURL[:index+1]
    print(baseURL)
elif not inputURL.endswith('/'):
    baseURL = inputURL + '/'
else:
    baseURL = inputURL

urlsChecked = set()
urlsToCheck = {inputURL}

# Put inputURL to -1 so that it doesn't
# count the fact that we are starting there
# as a link.
linkCount = {inputURL: -1}

while len(urlsToCheck) > 0:
    currentURL = urlsToCheck.pop()
    urlsChecked.add(currentURL)
    print("Checking page (" + currentURL + ")")

    try:
        response = urllib.request.urlopen(currentURL)
    except urllib.error.HTTPError as error:
            print("** Error code", error.code, "for", currentURL)
            continue

    if response.status != 200 or not(response.headers["Content-Type"].lower().startswith("text/html")):
        continue

    the_page = response.read().decode("utf-8")

    data = re.findall('<a [^>]*href="([^"]*)"[^>]*>', the_page)
    for url in data:
        if len(url) == 0 or url[0] == '#':
            continue
        elif url[0] == '/':
            url = baseURL + url[1:]
        elif not url.startswith(baseURL):
            continue

        if url not in urlsChecked:
            urlsToCheck.add(url)

        if url in linkCount:
            linkCount[url] += 1
        else:
            linkCount[url] = 1

linksTuples = []
for key in linkCount:
    linksTuples.append((linkCount[key], key))

linksTuples.sort()
for urlTuple in linksTuples:
    print(urlTuple)
