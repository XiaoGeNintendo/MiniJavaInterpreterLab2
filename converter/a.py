with open("in.txt","r",encoding="utf-8") as f:
    line = "".join(f.readlines())
    out=line.replace("System.out.println","println").replace("System.out.print","print") \
          .replace("String","string").replace("abstract","").replace("public","").replace("private","") \
          .replace("@Override","").replace("static void","int").replace("main(string[] args)","main()").replace("double","int").replace("tostring","to_string")

#     if "double" in out or "float" in out:
#         print("WARNING: double is not supported, please use int instead")
    print(out)
