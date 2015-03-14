


# 1. Installation #


# 2. Grundfunktionen #
## 2.1. Immer im Vordergrund ##

Die Tastatur kann wie eine ganz normale Tastatur bedient werden. Zu beachten ist hierbei, dass die
Tastatur immer im Vordergrund bleibt, aber nie im Fokus ist. Dies bedeutet, dass die Tastatur sich
über jede Anwendung herüber legen wird, allerdings der Cursor weiterihn im aktiven Fenster bleibt.
So ist gewährleistet, dass die Tastatur immer sichtbar ist und zugleich ein direktes Einfügen des
getippten in jeder möglichen Anwendung möglich ist. Der getippte Text wird immer genau da
eingefügt, wo der Cursor des aktiven Fensters ist. Tastenkürzel werden in der Anwendung
ausgeführt, welche zu dem Zeitpunkt den Fokus hat.

## 2.2. Vorschläge ##

Um eine schnellere Eingabe zu ermöglichen, schlägt die Tastatur Wortvervollständigungen vor.
Dies bedeutet, nachdem der erste Buchstabe getippt wurde, vervollständigt die Tastatur dies durch
ein Wort, welches vielleicht getippt werden möchte. Um einen Vorschlag anzunehmen, muss nur die
„Accept“-Leertaste gedrückt werden. Die Basis für diese Vorschläge sind alle Wörter die je getippt
wurden. Diese wurden inklusive ihrer Häufigkeit gespeichert.

## 2.3. Profilauswahl ##

Beim ersten Start beinhaltet die Tastatur nur ein Profil, das default Profil. Es können beliebig viele
Profile angelegt werden und über die DropDown Liste auf der Tastatur gewechselt werden. Jedes
Profil hat seine eigene Wortliste. Dies bedeutet, dass ein Wort, welches in einem Profil getippt
wurde, nicht in einem anderen Profil zur Vorschlagsfindung verwendet wird. Es wird empfohlen,
für verschiedene Sprachen, verschiedene Profile anzulegen um optimale Vorschläge zu
gewährliesten.

## 2.4. Vorschläge, Wortspeicherung ausschalten ##

Die Vorschlagsfunktion kann direkt auf der Tastatur ausgeschlatet werden. Dies geschieiht über den
Knopf „T10“. Weiterhin kann auch über den Button „Save“ die Erweiterung der Wortliste, also die
Speicherung der getippten Wörter ausgeschlatet werden. Dies ist insbesondere bei der Eingabe von
Passwörtern interessant.

## 2.5. Modi ##

Auf einer physischen Tastatur hat jede Taste mehrere Bedeutungen. Die Auswahl welche Bedeutung
verwendet werden soll geschieht über so genannte Modi (z.B. Shift). Diese werden gleichzeig mit
der gewünschten Taste gedrückt. Da ein gleichzeitiges Drücken auf der Bildschirmtastatur nicht
möglich ist, wird hier die Modi Taste erst ausgeführt, wenn eine weitere Taste gedrückt wurde. Zum
Beispiel, können Großbuchstaben dadurch erreicht werden, dass zuerst die Shift Taste gedrückt
wird und unmittelbar danach ein beliebiger Buchstabe. Alternativ ist es auch möglich eine Taste mit
der rechten Maustaste zu drücken, auch dies erzeugt einen Großbuchstaben.

Wurde eine Buchstabentaste gedrückt, wird der Shift Modus wieder verlassen. Sind längere Phasen
von Großbuchstaben erwünscht, so kann der Shift Modus fest eingestellt werden. Dafür muss eine
Shift Taste 2x betätgit werden.


# 3. Menü #
## 3.1. File ##

> ### New Profile ###
    * legt ein neues Profil an
> ### Import Profile ###
    * importiert ein Profil
    * Vorsicht: nur Profile, welche aus einer T10 Tastatur exportiert wurden, können importiert werden.
> ### Export Profile ###
    * exportiert ein Profil
> ### Close ###
    * eendet die On-Screen Tastatur

## 3.2. Profile ##

> ### Modify ###
    * Bearbeitet die Einstellungen des aktiven Profils (Name, Pfad zum Profil, …)
> ### Extend Dictionary By Text ###
    * eine Textdatei muss angegeben werden
    * jedes Wort dieser Textdatei wird dem Wörterbuch des aktuellen Profils hinzugefügt
    * Empfehlung: Selbst getippte Texte vor Benutzung der Tastatur einlesen lassen um eine gewisse Grundbasis an Wörtern zu haben
    * Empfehlung: Clean Dictionary nach dieser Funktion auszuführen, damit nur Wörter im Wörterbuch sind, welche wirklich öfters getippt werden
> ### Extend Dictoinary From File ###
    * erweitert das momentane Profil, mit einem beliebigen, von der T10 Tastatur erstellten, Wörterbuch
    * Anwendungsfall: Profil „deutsch“ wird erstellt:
      * Extend Dictionary From File → von Profil „Arbeit“
      * Extend Dictionary From File → von Profil „Freizeit“
> ### Export Dictionary To File ###
    * exportiert das Wörterbuch des momentanen Profils in eine Datei
> ### Clean Dictionary bereinigt das Wörterbuch des momentanen Profils ###
    * löscht alle Wörter aus dem Wörterbuch die seltener eingegeben wurden als eine angebbare Anzahl bzw. älter sind als ein bestimmter Zeitpunkt
> ### Delete ###
    * löscht das momentane Profil


