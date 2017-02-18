package util;

import java.io.*;

/**
 * Interface einer Reader-Klasse zum strukturierten Einlesen von Streams.
 * 
 * @version	1.10	29.11.98
 * @version	1.20	07.03.99	setTerminatingChar
 * @version 1.30	10.10.99	readWord
 * @author Thomas Brinkhoff
 */
public interface EntryInput {
	

/**
 * Gibt zur�ck, ob das Ende des Streams / der Datei erreicht wurde.
 * @return Ende erreicht?
 */
public boolean eof();
/**
 * Gibt zur�ck, ob das Ende der Zeile erreicht wurde.
 * @return Ende erreicht?
 */
public boolean eol();
/**
 * Gibt zur�ck, ob bei der letzten Lese-Operation ein numerischer Fehler erfolgt ist.
 * @return numerischer Fehler?
 */
public boolean numErr();
/**
 * Liest einen Boolean. Dabei entspricht 1 true; ansonsten wird false geliefert.
 * @return der gelesene Wert
 */
public boolean readBoolean();
/**
 * Liest ein Zeichen.
 * @return das gelesene Zeichen
 */
public char readChar();
/**
 * Liest eine Double-Zahl. Tritt ein Fehler auf, wird 0 zur�ckgegeben
 * und errNum() gibt true zur�ck.
 * @return die gelesene Zahl
 */
public double readDouble();
/**
 * Liest eine Integer-Zahl. Tritt ein Fehler auf, wird 0 zur�ckgegeben
 * und errNum() gibt true zur�ck.
 * @return die gelesene Zahl
 */
public int readInt();
/**
 * Liest eine Long-Zahl. Tritt ein Fehler auf, wird 0 zur�ckgegeben
 * und errNum() gibt true zur�ck.
 * @return die gelesene Zahl
 */
public long readLong();
/**
 * Liest eine Long-Zahl. Tritt ein Fehler auf, wird 0 zur�ckgegeben
 * und errNum() gibt true zur�ck.
 * @return die gelesene Zahl
 */
public short readShort();
/**
 * Liest eine Zeichenkette ein.
 * @return eingelesene Zeichenkette; ggf. null
 */
public String readString();
/**
 * Liest ein Wort, das aus 2 Byte besteht zur�ck.
 * Wird das Dateiende �berschritte, wird 0 zur�ckgegeben.
 * @return der gelesene Wert als vorzeichenbehaftete Zahl
 */
public short readWord2();
/**
 * Liest ein Wort, das aus 4 Byte besteht zur�ck.
 * Wird das Dateiende �berschritten, wird 0 zur�ckgegeben.
 * @return der gelesene Wert als vorzeichenbehaftete Zahl
 */
public int readWord4();
/**
 * Setzt die L�nge des n�chsten einzulesenden Eintrags.
 * @param length Eintragsl�nge, 0 = beliebig
 */
public void setNextEntryLength(int length);
/**
 * Setzt das Abschlu�zeichen (Default = '\t').
 * @param t Abschlu�zeichen
 */
public void setTerminatingChar(char t);
/**
 * �berspringt n Zeichen.
 * @return Anzahl der tats�chlich �bersprungenen Zeichen
 * @param n Anzahl der Zeichen
 */
public long skip(long n) throws IOException;
}
