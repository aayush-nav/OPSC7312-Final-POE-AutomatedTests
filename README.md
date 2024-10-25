
<h1 align="center">OPSC7312 POE Part 2 - Vitaflex - The A Team POE 👋</h1>

## Vitaflex

### Table of Contents
- [Description of the app](#description-of-the-app)
- [Features](#features)
- [Design Considerations](#design-considerations)
  - [User Interface (UI) and User Experience (UX)](#user-interface-ui-and-user-experience)
  - [Data Handling](#data-handling)
  - [Testing and Quality Assurance](#testing-and-quality-assurance)
- [Utilisation of GitHub and GitHub Actions](#utilisation-of-github-and-github-actions)
  - [Version Control with GitHub](#version-control-with-gitHub)
  - [Continuous Integration and Continuous Deployment (CI/CD) with GitHub Actions](#continuous-integration-and-continuous-deployment-ci-cd-with-github-actions)
- [Benefits of Using GitHub and GitHub Actions](#benefits-of-using-github-and-github-actions)
- [Contributors](#contributors)
- [Installation](#installation)
- [Usage](#usage)
- [Authors and Acknowledgement](#authors-and-acknowledgement)
- [Support](#support)
- [Project Status](#project-status)






## Description of the app

With the help of the *Vitaflex* app, users can manage their daily caloric intake and keep an eye on their weight to support a balanced and healthy lifestyle. The software offers a comprehensive platform for people to meet their health goals and promote long-term wellness by making food management easier.

<p align="right">(<a href="#table-of-contents">back to top</a>)</p>



## Features

* Custom Food and Recipes: Nutrition management is made simple for users by allowing them to log and track custom foods and recipes.
* Personalised Goal Setting: Users can define their health and fitness goals such as weight loss, muscle gain, or maintenance.
* Detailed Progress Tracking: The app provides real-time data on dietary habits, enabling users to monitor their progress with graphs and reports.
* Real-Time Notifications: Custom notifications help users stay on track with meal timings.

<p align="right">(<a href="#table-of-contents">back to top</a>)</p>



## Design Considerations

When designing the Vitaflex app, several key considerations were made:

### User Interface (UI) and User Experience (UX)
Intuitive Navigation:
 The app features a simple and intuitive navigation structure to enhance user experience. Users can easily access meal tracking and recipe sections.

Responsive Design: The UI is designed to be responsive across various devices, ensuring a consistent experience on smartphones and tablets.

### Data Handling
API Integration: The app integrates two APIs:
* Custom API: Handles user-generated data, such as meals and recipes.
* CalorieNinja API: Provides additional nutritional information and meal suggestions.

Data Models: Defined data classes (e.g., Meal, Ingredient) facilitate data management and ensure that the app correctly processes and displays information.

### Testing and Quality Assurance
* Unit Testing: Unit tests are implemented to verify the functionality of critical features, such as API interactions and data handling.
* Automated Testing: Integration tests are executed on every push and pull request to ensure consistent performance and reliability of the app.

<p align="right">(<a href="#table-of-contents">back to top</a>)</p>



## 3. Utilisation of GitHub and GitHub Actions

### Version Control with GitHub

GitHub is utilized as the version control system for the Vitaflex app, enabling collaboration among team members and efficient management of the codebase. Key practices include:

* Branching Strategy: Features and fixes are developed on separate branches, allowing for organized code management and easier merging.
* Pull Requests: Code reviews are conducted through pull requests, fostering collaboration and ensuring code quality.

### Continuous Integration and Continuous Deployment (CI/CD) with GitHub Actions
GitHub Actions is employed to automate testing and deployment processes, enhancing the development workflow. The following actions are configured in the .github/workflows folder:

#### GET Meals from the API:

* URL: https://recipe-meal-api-the-a-team.onrender.com/getmeals
* This action verifies that the API is accessible and functioning correctly, ensuring that users can retrieve their meals seamlessly.

#### GET Recipes from the API:

* URL: https://recipe-meal-api-the-a-team.onrender.com/getrecipes
* Similar to the meals endpoint, this action checks the availability of the recipe API, providing users with reliable access to diverse meal options.

#### Generate APK and AAB Files:

* Automated build processes are configured to generate APK (Android Package) and AAB (Android App Bundle) files during every pull and push request.
* This facilitates easy distribution and testing of the app, allowing team members to quickly access the latest build.

<p align="right">(<a href="#table-of-contents">back to top</a>)</p>


## Benefits of Using GitHub and GitHub Actions

* Streamlined Workflow: Automation of testing and deployment reduces manual effort and minimizes errors.

* Improved Collaboration: GitHub provides a platform for team collaboration, enhancing communication and project management.

* Rapid Feedback Loop: Automated tests ensure that any issues are identified early in the development process, leading to quicker resolutions.

<p align="right">(<a href="#table-of-contents">back to top</a>)</p>



## Installation

### Prerequisites

* Android Studio Installed
* Github

### Steps

1. Clone the repository.
bash
git clone

2. Open the project in Android Studio.
3. Run the application and launch it on an emulator or a connected Android smartphone.

<p align="right">(<a href="#table-of-contents">back to top</a>)</p>


## Usage

Users can register and set up fitness goals after the app is loaded. Use the dashboard for:

* Log meals and create custom recipes
* Monitor calorie consumption and progress through charts and graphs

<p align="right">(<a href="#table-of-contents">back to top</a>)</p>



## Authors and Acknowledgement

👤 **ST10048211** - Anjali Sunil Morar
👤 **ST10071160** - Aidan Johann Schwoerer
👤 **ST10104776** - Mohamad Aslam Mustufa Khalifa
👤 **ST10243270** - Aayush Navsariwala
👤 **ST10062860** - Abdullah Gadatia

- Github: [@The-A-Team](https://github.com/ST10048211)

- Project Link: [https://github.com/VCSTDN/prog7311-part-2-submission-ST10048211.git](https://github.com/VCSTDN/prog7311-part-2-submission-ST10048211.git)
- YouTube Link:  https://youtu.be/WI1oZTWEBoE]


<p align="right">(<a href="#table-of-contents">back to top</a>)</p>



## Support

Email - the.a.team.bcad@gmail.com

<p align="right">(<a href="#table-of-contents">back to top</a>)</p>

## Project Status

OPSC7312 - POE Part 2

<p align="right">(<a href="#table-of-contents">back to top</a>)</p>
