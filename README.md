# Med-Data

Med-Data is a mobile app that allows users to enter a list of medications in their household, from prescription drugs to over-the-counter. Whenever the name of their medication matches with an FDA drug recall, the user will be alerted. For example, if the user takes the blood pressure medication Losartan, they would have been alerted on April 26th of the expanded recall. Additionally, there is a real-time news feed that also brings food recalls to the user’s attention, such as the 56 tons of ground beef recalled this past week due to an E. coli outbreak.


Our app was coded using Android Studio. A large part of our app was also implementing the usage of Google Cloud. We used Firebase authentication to to store user login information. It stores user data (their login information and medication list) on Google Cloud’s Firebase, and implements openFDA, the FDA’s API, to parse data using JSON on food and drug recalls.
 

This project was made by Eric Ke, Nathan Zhao, Anne Xu, and Christina Leung in under 36 hours as a part of Citrus Hacks 2019.
