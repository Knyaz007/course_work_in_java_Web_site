/* // Фильтрует список комнат по выбранному отелю: показывает только те комнаты, которые относятся к выбранному отелю, остальные скрывает.
function filterRoomsByHotel(select) {


// Показывает информацию о выбранном отеле, заполняя соответствующие поля и показывая блок с информацией.
function showHotelInfo(select) {


// Показывает информацию о выбранной комнате, заполняя соответствующие поля и показывая блок с информацией.
function showRoomInfo(select) {


// Показывает информацию о выбранном рейсе, заполняя соответствующие поля и показывая блок с информацией.
function showFlightInfo(select) {


// Показывает информацию о выбранной экскурсии, заполняя соответствующие поля и показывая блок с информацией.
function showExcursionInfo(select) {


// Фильтрует список вагонов по выбранному железнодорожному билету: показывает только вагоны, относящиеся к выбранному билету.
function filterCarriagesByTicket(select) {


// Показывает информацию о выбранном железнодорожном билете, заполняя соответствующие поля и показывая блок с информацией.
function showTrainTicketInfo(select) {


// Показывает информацию о выбранном вагоне, заполняя соответствующие поля и показывая блок с информацией.
function showCarriageInfo(select) {

 */






function filterRoomsByHotel(select) {
    const hotelId = select.value;
    const roomSelect = document.getElementById('room');
    const roomOptions = roomSelect.querySelectorAll('option');

    roomOptions.forEach(option => {
        if (option.value === "") {
            // placeholder всегда показываем
            option.style.display = '';
        } else if (option.dataset.hotelId === hotelId) {
            option.style.display = '';
        } else {
            option.style.display = 'none';
        }
    });

    roomSelect.selectedIndex = 0; // сбросить выбор комнаты
}



function showHotelInfo(select) {
    const option = select.options[select.selectedIndex];
    if (!option || !option.value) {
        document.getElementById("hotel-info").style.display = "none";
        return;
    }

    document.getElementById("hotel-name").textContent = option.dataset.name || '';
    document.getElementById("hotel-address").textContent = 
        (option.dataset.country || '') + ", " + 
        (option.dataset.city || '') + ", " + 
        (option.dataset.street || '') + " " + 
        (option.dataset.house || '');
    document.getElementById("hotel-type").textContent = option.dataset.type || '';
    document.getElementById("hotel-rooms").textContent = option.dataset.rooms || '';

    document.getElementById("hotel-info").style.display = "block";
}

function showRoomInfo(select) {
    const option = select.options[select.selectedIndex];
    if (!option || !option.value) {
        document.getElementById("room-info").style.display = "none";
        return;
    }

    document.getElementById("room-number").textContent = option.dataset.number || '';
    document.getElementById("room-capacity").textContent = option.dataset.capacity || '';
    document.getElementById("room-type").textContent = option.dataset.type || '';
    document.getElementById("room-price").textContent = (option.dataset.price || '') + "₽";

    document.getElementById("room-info").style.display = "block";
}

 

 
function showFlightInfo(select) {
    const option = select.options[select.selectedIndex];
    if (!option || !option.value) {
        document.getElementById("flight-info").style.display = "none";
        return;
    }

    document.getElementById("flight-number").textContent = option.dataset.flightNumber || '';
    document.getElementById("flight-departure").textContent = option.dataset.departure || '';
    document.getElementById("flight-departure-airport").textContent = option.dataset.departureAirport || '';
    document.getElementById("flight-destination").textContent = option.dataset.destination || '';
    document.getElementById("flight-arrival-airport").textContent = option.dataset.arrivalAirport || '';
    document.getElementById("flight-departure-date").textContent = option.dataset.departureDate || '';
    document.getElementById("flight-departure-time").textContent = option.dataset.departureTime || '';
    document.getElementById("flight-arrival-date").textContent = option.dataset.arrivalDate || '';
    document.getElementById("flight-arrival-time").textContent = option.dataset.arrivalTime || '';
    document.getElementById("flight-available-seats").textContent = option.dataset.availableSeats || '';
    document.getElementById("flight-price").textContent = option.dataset.price || '';
    document.getElementById("flight-time").textContent = option.dataset.flightTime || '';
    document.getElementById("flight-wifi").textContent = option.dataset.wifi === 'true' ? 'Да' : 'Нет';
    document.getElementById("flight-payment-methods").textContent = option.dataset.paymentMethods || '';
    document.getElementById("flight-pet-policy").textContent = option.dataset.petPolicy || '';

    document.getElementById("flight-info").style.display = "block";
}
 
 
    function showExcursionInfo(select) {
        const option = select.options[select.selectedIndex];
        if (!option || !option.value) {
            document.getElementById("excursion-info").style.display = "none";
            return;
        }

        document.getElementById("exc-title").textContent = option.dataset.title || '';
        document.getElementById("exc-description").textContent = option.dataset.description || '';
        document.getElementById("exc-location").textContent = option.dataset.location || '';
        document.getElementById("exc-city").textContent = option.dataset.city || '';
        document.getElementById("exc-country").textContent = option.dataset.country || '';
        document.getElementById("exc-date-time").textContent = option.dataset.dateTime || '';
        document.getElementById("exc-price").textContent = option.dataset.price || '';
        document.getElementById("exc-tour").textContent = option.dataset.tour || '';

        document.getElementById("excursion-info").style.display = "block";
    }
 

 
function filterCarriagesByTicket(select) {
    const ticketId = select.value;
    const carriageSelect = document.getElementById('carriage');
    const options = carriageSelect.querySelectorAll('option');

    options.forEach(opt => {
        if (opt.value === "") {
            opt.style.display = ''; // show placeholder
        } else if (opt.dataset.trainTicketId === ticketId) {
            opt.style.display = '';
        } else {
            opt.style.display = 'none';
        }
    });

    carriageSelect.selectedIndex = 0;
    document.getElementById("carriage-info").style.display = "none";
}

function showTrainTicketInfo(select) {
    const option = select.options[select.selectedIndex];
    if (!option || !option.value) {
        document.getElementById("ticket-info").style.display = "none";
        return;
    }

    document.getElementById("ticket-train-number").textContent = option.dataset.trainNumber || '';
    document.getElementById("ticket-departure-city").textContent = option.dataset.departureCity || '';
    document.getElementById("ticket-departure-station").textContent = option.dataset.departureStation || '';
    document.getElementById("ticket-destination-city").textContent = option.dataset.destinationCity || '';
    document.getElementById("ticket-destination-station").textContent = option.dataset.destinationStation || '';
    document.getElementById("ticket-departure-date").textContent = option.dataset.departureDate || '';
    document.getElementById("ticket-departure-time").textContent = option.dataset.departureTime || '';
    document.getElementById("ticket-arrival-date").textContent = option.dataset.arrivalDate || '';
    document.getElementById("ticket-arrival-time").textContent = option.dataset.arrivalTime || '';
    document.getElementById("ticket-price").textContent = option.dataset.price || '';

    document.getElementById("ticket-info").style.display = "block";
}

function showCarriageInfo(select) {
    const option = select.options[select.selectedIndex];
    if (!option || !option.value) {
        document.getElementById("carriage-info").style.display = "none";
        return;
    }

    document.getElementById("carriage-type").textContent = option.dataset.type || '';
    document.getElementById("carriage-seats").textContent = option.dataset.seats || '';
    document.getElementById("carriage-price").textContent = option.dataset.price || '';
    document.getElementById("carriage-seat-type").textContent = option.dataset.seatType || '';
    document.getElementById("carriage-class").textContent = option.dataset.ticketClass || '';

    document.getElementById("carriage-info").style.display = "block";
}
 

 
function filterCarriagesByTicket(select) {
    const ticketId = select.value;
    const carriageSelect = document.getElementById('carriage');
    const options = carriageSelect.querySelectorAll('option');

    options.forEach(opt => {
        if (opt.value === "") {
            opt.style.display = ''; // show placeholder
        } else if (opt.dataset.trainTicketId === ticketId) {
            opt.style.display = '';
        } else {
            opt.style.display = 'none';
        }
    });

    carriageSelect.selectedIndex = 0;
    document.getElementById("carriage-info").style.display = "none";
}

function showTrainTicketInfo(select) {
    const option = select.options[select.selectedIndex];
    if (!option || !option.value) {
        document.getElementById("ticket-info").style.display = "none";
        return;
    }

    document.getElementById("ticket-train-number").textContent = option.dataset.trainNumber || '';
    document.getElementById("ticket-departure-city").textContent = option.dataset.departureCity || '';
    document.getElementById("ticket-departure-station").textContent = option.dataset.departureStation || '';
    document.getElementById("ticket-destination-city").textContent = option.dataset.destinationCity || '';
    document.getElementById("ticket-destination-station").textContent = option.dataset.destinationStation || '';
    document.getElementById("ticket-departure-date").textContent = option.dataset.departureDate || '';
    document.getElementById("ticket-departure-time").textContent = option.dataset.departureTime || '';
    document.getElementById("ticket-arrival-date").textContent = option.dataset.arrivalDate || '';
    document.getElementById("ticket-arrival-time").textContent = option.dataset.arrivalTime || '';
    document.getElementById("ticket-price").textContent = option.dataset.price || '';

    document.getElementById("ticket-info").style.display = "block";
}

function showCarriageInfo(select) {
    const option = select.options[select.selectedIndex];
    if (!option || !option.value) {
        document.getElementById("carriage-info").style.display = "none";
        return;
    }

    document.getElementById("carriage-type").textContent = option.dataset.type || '';
    document.getElementById("carriage-seats").textContent = option.dataset.seats || '';
    document.getElementById("carriage-price").textContent = option.dataset.price || '';
    document.getElementById("carriage-seat-type").textContent = option.dataset.seatType || '';
    document.getElementById("carriage-class").textContent = option.dataset.ticketClass || '';

    document.getElementById("carriage-info").style.display = "block";
}
 
