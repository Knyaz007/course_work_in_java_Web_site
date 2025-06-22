// Обновление значения из datalist
function updateInputValue() {
  document.getElementById('Region').value = document.getElementById('citiesSelect').value;
}

// Обновление количества гостей
var adultsCount = 2;
function updateGuests(change) {
  if (adultsCount + change >= 0 && adultsCount + change <= 4) {
    adultsCount += change;
    document.getElementById('guestsInput').value = 'Нужно на ' + adultsCount;
  }
}

// Отправка формы поиска
function submitForm() {
  document.getElementById('cityInput').value = document.getElementById('Region').value;
  document.getElementById('search-form').submit();
}

// Обработка клика по карточке отеля
function handleClick(event, hotelId) {
  if (window.getSelection().toString().length > 0) {
    return;
  }
  window.location.href = '/hotels/details/' + hotelId;
}

// Функция для отображения предыдущей фотографии отеля
function showPrevPhoto(element, hotelId) {
  var totalPhotos = element.getAttribute('data-total-photos');
  var carousel = element.closest('.photo-carousel');
  var container = carousel.querySelector('.photo-container');
  var photos = container.children;
  var currentIndex = getCurrentIndex(container);
  var newIndex = (currentIndex - 1 + photos.length) % photos.length;
  showPhotoAtIndex(container, newIndex);
}

// Функция для отображения следующей фотографии отеля
function showNextPhoto(element, hotelId) {
  var totalPhotos = element.getAttribute('data-total-photos');
  var carousel = element.closest('.photo-carousel');
  var container = carousel.querySelector('.photo-container');
  var photos = container.children;
  var currentIndex = getCurrentIndex(container);
  var newIndex = (currentIndex + 1) % photos.length;
  showPhotoAtIndex(container, newIndex);
}

// Получить текущий индекс показанной фотографии
function getCurrentIndex(container) {
  var photos = container.children;
  for (var i = 0; i < photos.length; i++) {
    if (!photos[i].style.display || photos[i].style.display !== 'none') {
      return i;
    }
  }
  return 0;
}

// Показать фото по индексу
function showPhotoAtIndex(container, index) {
  var photos = container.children;
  for (var i = 0; i < photos.length; i++) {
    photos[i].style.display = i === index ? 'block' : 'none';
  }
}
