I know some of the variable names are kinda fucked up. please forgive me. My sanity could not keep up with continuity for the 3 hours I've been working on this to quickly push this out lol.
I've made the code as modular as i can
Scraper.java is the main code, that scrapes using a loop
Taskurl.java is called in scraper.java, to get the task urls from the contest url that is given in scraper.java
Taskdata.java is the code that actually scrapes some of the data in the task page. This is not complete yet, as i have not dealt with constraints, samples and whatnot. This will be done promptly at a future date
Mongo.java is the function that takes the data from taskdata.java and actually puts it into the database we have in mongoDB.

Thank you for reading this far. This was my first time writing something that connects to a database. please be forgiving on the comments... or else... >:^(