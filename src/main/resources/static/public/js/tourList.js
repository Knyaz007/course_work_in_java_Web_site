 
          var adultsCount = 2;
          function updateGuestsInput() {
            document.getElementById('guestsInput').value = 'Нужно на ' + adultsCount;
          }
          function updateGuests(change) {
            adultsCount = Math.max(1, Math.min(4, adultsCount + change));
            updateGuestsInput();
          }
    
