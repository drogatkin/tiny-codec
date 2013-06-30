package net.didion.loopy;

import java.util.Enumeration;
import java.io.File;

import net.didion.loopy.iso9660.ISO9660FileSystem;
public class Main {
   public static void main(String ...args) {
       if (args.length == 0) {
         usage();
         System.exit(-1);
       }
   try {
       ISO9660FileSystem fs = new ISO9660FileSystem(new File(args[0]), true);
       Enumeration es = fs. getEntries();
       while(es.hasMoreElements()) {
        System.out.printf("->>%s%n", es.nextElement());
       }
   }catch(Exception e) {
    e.printStackTrace();
   }
   }

   static void usage() {
    System.out.printf("Usage: java net.didion.loopy.Main <input.iso> [<extract.file>]%n");

   }
}