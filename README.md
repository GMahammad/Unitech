  <h1>UniTech App</h1>
    <h2>Description</h2>
    <p>
      Unitech app is a banBking app which is mdp product of the banking services
      and back-end service
    </p>
    <h2>Installing</h2>
    <p>Firstly you have to clone this github repo to your local machine. Also <b>Docker Desktop</b> should be installed in your PC</p>
    <h2>Usage</h2>
    <p>
        <ul>
            <li>
                <b>Step 1:</b> <br>
                Initially open a local terminal, change directory to the exact github repository. In the root component you have to run <b>docker-compose up -d</b>  command. This command will fetch images and create Docker container inside of the Docker Desktop application.
            </li>
            <li> <b>Step 2:</b> <br>            After creating docker container you have to change direction to(there are 2 module unitech and currencyapi) unitech folder inside of this style Rectory you have to run <b>mvn spring-boot:run</b>  command and this command will compile our main unitech module.
            </li>
            <li>
                <b>Step 3:</b> <br>
                As a next step you have to open a new terminal and change directory to the main folder which you cloned from github. For this time change directory to currencyapi folder and run mvn spring-boot:run command. This will compile the third-party Currency API mock service.
            </li>
           <li><b>Final Step:</b> <br>
                You have already compiled both modules and right now you can test them with sending request to specific paths. I added below all pathes which can be tested by any one.
                Moreover, when main module compiled application insert DUMMY_DATA to database.I provided below login information, then you can use them for your testing purposes.
                <br>ADMIN -> <br>
                {<br>
                    "pin": "admin",<br>
                    "password": "password" <br>
                } <br>
                USER -> <br>
                {
                    <br>
                    "pin": "user",<br>
                    "password": "Mahammad" <br>
                }
            </li>
        </ul>
    </p>
      <h2>Roles</h2>
    <p>There are 2 roles(Admin & User) inside of the Unitech app. Each role has specific features.</p>
    <h2>Main Functionalities</h2>
       <p>
        <ul>
            <li>Authentication (Register&Login) powered by jwt token: <br> User can register and login with  PIN and password which is chosen by user the Chosen password will be encrypted when it stores in the database. Also after registration and login process user get a jwt token for authorization purposes. This token has expiration date and after 48 hours jwt token is expired automatically.</li>
            <li>Bank Accounts: <br> User can get all active accounts if they have. Only admin can create account and also change the status of account.</li>
            <li>Make transfer: <br>User can send money among own  accounts and others' accounts too. Each account has accountCurrency and if sender and receiver account currencies are the same, service doesn't call any third party API for converting currency.However if the currencies are different then service will send the request to the third party API(currencyAPI) and get the exchange rate (i.e - USD/AZN = 1.7) for converting money.
            After converting process the money will be transferred to receiver account.
            </li>
          <li>
            Currency API: <br> This is third party mock service where user can get random information(because of testing purposes.Period was handled in another service) with given inputs(i.e. from:USD, to:AZN) about current exchange rate and convert money while transfering money. Let's assume that each request to this API has cost and the bank should decrease request count. Another service handle this problem.
          </li>
          <li>CurrencyCacheService: <br> 
              For increasing efficiency and reducing cost Spring Cache system was used here. Cache is internal data storage where service can store some data and called them. 
            In our app, CurrencyCacheService has own functionalities for reducing cost and incresing efficiency. <br>
            Let's explain with a real world example:
            When user want to send money from USD account to AZN account. He send a request and our service fetch data for USD/AZN and store it in cache. When it fetch USD/AZN, service automatically convert USD/AZN to AZN/USD and store it in cache too. 
            In this way, we reduce request count 2 times. Moreover, next 1-minute period both values are stored in cache and during this time frame if other requests also come service does not send new request and get data from cache.
            After 1 minute cache clear itself automatically and it helps us for displaying up-to-date exchange rate to users. Finally, if user does not send new request API will not be called by service and we reduce cost and increase efficiency for our application.
          </li>
        </ul>
    </p>
    <h2>Testing</h2>
    <p>
      Reliability of Unitech app is provided with unit and integration tests. The minimum coverage for our app is apprx 70%. There are 24 tests and if you want to run these test you have to change directory to main folder(Unitech). After that you have to run <b>mvn test</b> command, so this command will build all test cases. 
    </p>










    
