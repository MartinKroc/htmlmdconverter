# Podsumowanie etapu 4
## 1. Co zostało zrobione
### 1.1 Poznanie biblioteki Jsoup i terminologii związanej z parsowaniem i serializacją

* JSOUP - Jsoup przede wszystkim korzysta z obiektów typu Document. Reprezentuje on drzewo dokumentu i umożliwia dostanie się do poszczególnych węzłów kodu źródłowego HTML. Oznacza to dostęp do drzewa DOM ( Document Object Model) jak w przeglądarce internetowej. 
* Serializacja - W serializacji zamienia się strukturę obiektową na dane testowe. Wówczas pewna hierarchiczna struktura danych lub obiektów przekształcana jest do postaci szeregowej, czyli formatu, który może być przechowywany, z zachowaniem aktualnego stanu obiektów. W przypadku webu najpopularniejszym formatem dla serializacji jest JSON (dawniej prym wiódł XML). Taka zserializowana postać obiektu może być przechowywana, np. w buforze pliku lub pamięci, przesyłana do innego procesu lub innego komputera poprzez Sieć, i w późniejszym czasie odtwarzana w tym samym lub innym środowisku komputerowym. Kiedy otrzymany szereg bitów jest odczytywany zgodnie z formatem serializacji, to może być wykorzystywany do utworzenia semantycznie identycznych klonów dla oryginalnych obiektów. Dla wielu złożonych obiektów, np. takich które w szerokim zakresie używają referencji, proces ten nie należy do najłatwiejszych. Serializacja obiektów zorientowanych obiektowo nie zawiera żadnych związanych z nimi metod, z którymi wcześniej były ze sobą nierozerwalnie powiązane. Procesem stricte odwrotnym do serializacji jest deserializacjaPolega ona na odczytaniu wcześniej zapisanego strumienia danych i odtworzeniu na tej podstawie obiektu wraz z jego stanem bezpośrednio sprzed serializacji.
* Parsowanie - Parsowanie polega na zamianie danych wejściowych (test) na odpowiadającą im strukturę obiektową ( wyjście). Dokonuje się wówczas analizy testu celem ustalenia pewnej struktury i zgodności ze składnią języka. Zastosowanie parserów jest silnie uzależnione od samego wejścia (danych wejściowych). W przypadku języków danych (np. znacznikowego HTML lub XML) parser najczęściej występuje jako program ułatwiający czytanie plików. W przypadku języków programowania parser występuje jako komponent dla kompilatora lub interpretera. Analizuje on kod źródłowy w postaci jakiegoś języka programowania w celu utworzenia wewnętrznej reprezentacji.

* Źródła: http://webref.pl, https://javastart.pl
 
 ### 1.2
 Umieszczono biblioteke Jsoup w projekcie, w warstwie logiki. Tam otwierany jest plik otrzymany od użytkownika. Plik zostaje zparsowany metodą Jsoup.parse(). Dzięki temu uzyskany jest dostęp do struktury dokumentu. Z niego wyciągnięte zostają znaczniki. 

 ### 1.3 
Zaimplementowano dodatkowy endpont, którego działanie polega na pobraniu pliku z serwera do klienta. Na razie, w celach testów jest to plik w formacie HTML. W przysłości odesłany zostanie plik w formacie MD. 

## 2. Co wykonam w następnym etapie
* Implementacja konwersji pliku HTML na MD i ulepszenie mechanizmu pobierania pliku.
* Podgląd na zawartość wgranego i skonwertowanego pliku u klienta.