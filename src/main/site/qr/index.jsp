<%@
  page contentType="text/html; charset=UTF-8"
  import="java.util.*"
  import="java.io.*"
  import="java.net.*"
  import="java.lang.*"
  import="java.util.stream.*"
  import="com.fasterxml.jackson.databind.*"
%>
<%!
  private final static String CGI_NAME = "index.jsp";
  private final static String CGI_TITLE = "QR Code View";
  private final static String JSP_VERSION = "0.0.1";

  private final static String CONFIG_PARAM_QR_SERVICE = "QR_SERVICE_HREF";

  private final static String TITLE_FORMAT = "Карточка %s";
  private final static String HREF_QR_FORMAT = "%s/v1/api/unsecured/qrs?q=%s";
  private final static String HREF_FILE_FORMAT = "/qrCodeDemo/v1/api/unsecured/files/%d/download/%s";
  // private final static String HREF_FILES_FORMAT = "%s/v1/api/unsecured/qrs/files/all/download?q=%s";

  private final static String NO_QR_DATA_TEXT = "Нет информации для этой карточки";

  public static String[] HTML_UNSAFE_CHARACTERS = {"<",">","&","\n"};
  public static String[] HTML_UNSAFE_CHARACTERS_SUBST = {"&lt;","&gt;","&amp;","<br>\n"};
  public final static String[] VALUE_CHARACTERS = { "<",">","&","\"","'" };
  public final static String[] VALUE_CHARACTERS_SUBST = {"&lt;","&gt;","&amp;","&quot;","&#039;"};

  public static class QRDto {
    private Long code;
    private Map<String, String> data;
    private FormDto form;
    private List<FileDto> files;

    public Long getCode() {
      return code;
    }

    public void setCode(Long code) {
      this.code = code;
    }

    public Map<String, String> getData() {
      return data;
    }

    public void setData(Map<String, String> data) {
      this.data = data;
    }

    public FormDto getForm() {
      return form;
    }

    public void setForm(FormDto form) {
      this.form = form;
    }

    public List<FileDto> getFiles() {
      return files;
    }

    public void setFiles(List<FileDto> files) {
      this.files = files;
    }
  }

  public static class FormDto {
    private String data;
    private List<FormFieldDto> fields;
    private List<FileDto> files;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<FormFieldDto> getFields() {
        return fields;
    }

    public void setFields(List<FormFieldDto> fields) {
        this.fields = fields;
    }

    public List<FileDto> getFiles() {
        return files;
    }

    public void setFiles(List<FileDto> files) {
        this.files = files;
    }
  }

  public static class FormFieldDto {
    private String name;
    private String caption;
    private String placeholder;
    private Integer fieldOrder;
    private FormFieldType fieldType;
    private Boolean isStatic;
    private Boolean isPublic;

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public Integer getFieldOrder() {
        return fieldOrder;
    }

    public void setFieldOrder(Integer fieldOrder) {
        this.fieldOrder = fieldOrder;
    }

    public FormFieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FormFieldType fieldType) {
        this.fieldType = fieldType;
    }

    public Boolean getIsStatic() {
        return isStatic;
    }

    public void setIsStatic(Boolean isStatic) {
        this.isStatic = isStatic;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
  }

  public static class FileDto {
    private Long id;
    private String name;
    private String fileName;
    private String fileType;
    private String extension;
    private String checksum;
    private Long fileSize;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
  }

  public enum FormFieldType {
    TEXT,
    NUMBER,
    FILE,
    MEDIA_FILE,
    DATE,
    URL,
    PHONE,
    EMAIL
  }


public static class WebApp {
  private JspWriter out = null;
  private String qrHref = null;

  public WebApp(JspWriter out, String qrHref) {
    this.out = out;
    this.qrHref = qrHref;
  }

  private void printQRData(QRDto dto) throws IllegalArgumentException {
    FormDto form = dto.getForm();
    Map<String, String> data = dto.getData();

    int printedLines = 0;
    // Attributes print
    if (form != null) {
      printedLines = printAttributesTable(form, data);
    }

    // Files print
    List<FileDto> files = new ArrayList();
    List<FileDto> qrFiles = dto.getFiles();
    if (qrFiles != null) {
      files.addAll(qrFiles);
    }
    if (form != null) {
      List<FileDto> formFiles = form.getFiles();
      if (formFiles != null) {
        files.addAll(formFiles);
      }
    }
    if (!files.isEmpty()) {
      printFilesTable(dto.getCode(), files);
    }
    // Print if no data found as files/attributes
    if (printedLines == 0 && files.isEmpty()) {
      printNoQRData();
    }
  }

  private int printAttributesTable(FormDto form, Map<String, String> data)
      throws IllegalArgumentException {
    if (form == null) {
      throw new IllegalArgumentException("Не найдено шаблона");
    }
    List<FormFieldDto> fields = form.getFields();
    if (fields == null || fields.isEmpty()) {
      throw new IllegalArgumentException("Не найдено полей шаблона");
    }
    fields = fields.stream()
        .sorted(Comparator.nullsFirst(Comparator.comparingInt(FormFieldDto::getFieldOrder)))
        .collect(Collectors.toList());

    int printedLines = 0;
    startTable();
    for (FormFieldDto field : fields) {
      startTr();
      String fieldName = field.getName();
      String fieldCaption = field.getCaption();
      FormFieldType fieldType = field.getFieldType();
      String fieldValue = "";
      Boolean isStatic = field.getIsStatic();
      if (isStatic != null && isStatic) {
        fieldValue = field.getPlaceholder();
      }
      if (data != null) {
        String dataVal = data.get(fieldName);
        if (!(dataVal == null || dataVal.isEmpty())) {
          fieldValue = dataVal;
        }
      }
      if ((fieldValue != null && !fieldValue.isEmpty()) || isStatic) {
        startTd();
        if (fieldCaption == null || fieldCaption.isEmpty())  {
          w(text2html(fieldName));
        } else {
          w(text2html(fieldCaption));
        }
        endTd();
        startTd();
        w(formatByFieldType(fieldValue, fieldType));
        endTd();
        endTr();
        printedLines++;
      }
    }
    endTable();
    return printedLines;
  }

  private void printFilesTable(Long code, List<FileDto> files) {
    if (files == null || files.isEmpty()) {
      return;
    }
    // a("Скачать всё", String.format(HREF_FILES_FORMAT, qrHref, Long.toHexString(code)));

    startTable("files_table");
    for (FileDto file : files) {
      startTr("fileRow");
      Long id = file.getId();
      String name = file.getName();
      String fileName = file.getFileName();
      if (name == null || name.isEmpty()) {
        name = fileName;
      }
      startTd();
      printFileDiv(text2html(name), String.format(HREF_FILE_FORMAT, id, text2html(fileName)));
      endTd();
      endTr();
    }
    endTable();
  }

  private void startTable() {
    w("<table class='compact_table'>");
  }

  private void startTable(String id) {
    if (id == null || id.isEmpty()) {
      startTable();
      return;
    }
    w("<table class='compact_table' " + "id='" + id + "''>");
  }


  private void endTable() {
    w("</table>");
  }

  private void startTr(String className) {
    if (className == null || className.isEmpty()) {
      startTr();
      return;
    }
    w(String.format("<tr class='%s'>", className));
  }

  private void startTr() {
    w("<tr>");
  }

  private void endTr() {
    w("</tr>");
  }

  private void startTh() {
    w("<th>");
  }

  private void endTh() {
    w("</th>");
  }

  private void startTd() {
    w("<td>");
  }

  private void endTd() {
    w("</td>");
  }

  private void printNoQRData() {
    printTextOnPage(NO_QR_DATA_TEXT);
  }

  private void printTextOnPage(String text) {
    w(String.format("<div class='code_block'><h1>%s</h1></div>", text2html(text)));
  }

  private void printSearchForm() {
    w("<div class='code_block'>");
    w("<h1>Найдите информацию:</h1>");
    w("<form>");
    w("q: <input type='text' size='8' name='q' value='1070000'/>");
    w("<input type='submit'/ value='Искать'>");
    w("</form>");
    w("</div>");
  }

  private void printFileDiv(String name, String ref) {
    if (ref == null || ref.isEmpty()) {
      return;
    }
    String finalName = name == null ? "Без имени" : name;
    a(name, ref);
  }

  private void a(String name, String ref) {
    if (ref == null || ref.isEmpty()) {
      return;
    }
    String finalName = name == null ? "Без имени" : name;
    w(String.format("<a href='%s' target='_blank'>%s</a>", text2value(ref), text2html(finalName)));
  }

  private void w(String str) {
      try {
        out.println(str);
      } catch (IOException ex) {
        ex.printStackTrace();
      }
  }

 public static String text2html(String text) {
    return translate_tokens(text, HTML_UNSAFE_CHARACTERS, HTML_UNSAFE_CHARACTERS_SUBST);
  }

  public static String text2value(String text) {
    return translate_tokens(text, VALUE_CHARACTERS, VALUE_CHARACTERS_SUBST);
  }

 public static String translate_tokens(String sz, String[] from, String[] to) {
    if(sz == null) {
      return(sz);
    }
    StringBuffer sb = new StringBuffer(sz.length() + 256);
    int p = 0;
    while(p < sz.length()) {
      int i = 0;
      while (i < from.length) {
        if(sz.startsWith(from[i], p)) {
          sb.append(to[i]);
          p = --p + from[i].length();
          break;
        }
      i++;
      }
      if(i>=from.length)
        sb.append(sz.charAt(p));
      p++;
    }
    return sb.toString();
  }

   private String formatByFieldType(String value, FormFieldType fieldType) {
      if (fieldType == null) {
        return text2html(value);
      }
      if (FormFieldType.URL.equals(fieldType)) {
      String prefix = "";
      if (value != null &&
          !(value.startsWith("http://")
          || value.startsWith("https://")
          || value.startsWith("ftp://"))
      ) {
          prefix = "https://";
      }
        return String.format("<a href='%s' target='_blank'>%s</a>", text2value(prefix + value), text2html(value));
      } else if (FormFieldType.PHONE.equals(fieldType)) {
        return String.format("<a href='tel:%s' target='_blank'>%s</a>", text2value(value), text2html(value));
      } else if (FormFieldType.EMAIL.equals(fieldType)) {
        return String.format("<a href='mailto:%s' target='_blank'>%s</a>", text2value(value), text2html(value));
      } else {
        return text2html(value);
      }
   }
}

  private ObjectMapper getObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper;
  }

  public void inits(ServletConfig config) throws ServletException {
    super.init(config);

    String value = config.getInitParameter(CONFIG_PARAM_QR_SERVICE);
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("Configure qr service url in configuration");
    }
  }
%>
<%

 long enter_time = System.currentTimeMillis();
 long expire_time = enter_time + 24*60*60*1000;
 response.setHeader("Cache-Control","No-cache");
 response.setHeader("Pragma","no-cache");
 response.setDateHeader("Expires",expire_time);
 request.setCharacterEncoding("UTF-8");
 String qrHref = getServletContext().getInitParameter(CONFIG_PARAM_QR_SERVICE);
 String thisSiteHref = request.getServerName();
 WebApp app = new WebApp(out, thisSiteHref);
 String q = request.getParameter("q");
 String titleText = q == null ? "[empty]" : q;

%>

<html>
 <head>
  <title><%= String.format(TITLE_FORMAT, titleText) %></title>
  <meta name="google" content="notranslate">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <style>
    *, :after, :before, header {
      padding: 0;
      margin: 0;
      box-sizing: border-box;
      background-repeat: no-repeat;
    }

    html {
      height: 100%;
    }

    body {
      display: flex;
      flex-direction: column;
      height: 100%;
    }

    main {
      flex: 1;
      max-width: 100vw;
      display: block;
      background-color: var(--bg-color, #fff);
    }

    #main_block {
      padding: 8px;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
    }

    h1 {
      display: block;
      font-size: 2em;
      margin-block-start: 0.67em;
      margin-block-end: 0.67em;
      margin-inline-start: 0px;
      margin-inline-end: 0px;
      font-weight: bold;
      font-family: 'Attractive Heavy', sans-serif;
    }

    .code_block {
      text-align: center;
      justify-content: center;
      height: 100%;
      display: flex;
      flex-direction: column;
    }

    .compact_table {
        width: 100%;
        border: none;
        border-radius: 0.7rem;
        border-collapse: collapse;
        margin: 18px 0;
        table-layout: fixed;
        font-size: 0.9em;
        font-family: sans-serif;
        min-width: 200px;
        box-shadow: 0 0 20px rgba(0, 0, 0, 0.15);
    }

    .compact_table th {
        background-color: var(--table-header-background-color, #fff);
        color: var(--table-header-color, #fff);
        text-align: left;
    }

    .compact_table th,
    .compact_table td {
        padding: 12px 15px;
        border: none;
    }

    .compact_table tr td {
        border-bottom: thin solid var(--table-body-bottom-border-color, #E0E0E0);
        color: var(--table-body-color, #1f1f1f);

        overflow: hidden;
        word-break: break-word;
        overflow-wrap: break-word;
    }

    .compact_table tr td:nth-of-type(even) {
        background-color: var(--bg-secondary-color, #f8f8f8);
    }

    .compact_table tr:last-of-type {
        border-bottom: 2px solid var(--table-body-bottom-border-color, #E0E0E0);
    }

    .compact_table tr:hover td {
        background-color: var(--table-body-hover-color, #E3F2FD);
    }

    .compact_table tr td .custom_button {
        border: thin solid var(--alpha, rgb(0, 0, 0, 1));
    }

    tr.fileRow {
        cursor: pointer;

        td {
            text-decoration: none;
            color: var(--a-color, #000);
            text-align: center;
        }
    }

    table, td, th {
      text-align: left;
      border: 1px black solid;
      padding: 1px;
      overflow: hidden;
      word-break: break-word;
      overflow-wrap: break-word;
      width: 100%;
      font-size: 1.4em;
    }

    @media only screen and (max-width: 1000px) {
      table, td, th {
          margin: 2px;
          padding: 1px;
          font-size: 1.1em;
      }
      header {
          font-size: 2.0em;
      }
      h1 {
          font-size: 1.3em;
      }
      .big_button {
          width: 100%;
      }
    }

    footer {
      width: 100%;
      min-height: 50px;
      background-color: var(--bg-secondary-color, #f8f8f8);
    }

    .footer-inner {
      margin: 16px;
      display: flex;
      flex-direction: row;
      flex-wrap: wrap;
      justify-content: space-between;

      nav ul {
        display: flex;
        flex-direction: row;
      }
    }

    li {
      list-style: none;

      a {
        text-decoration: none;
      }
    }

    .gap-y-s8 {
      row-gap: 0.5rem;
      column-gap: 1rem;
    }
  </style>
 </head>
<body>
  <main>
  <div id="main_block">
 <%
   String json = new String("");
   if (q != null) {
    URLConnection con;
    try {
      URL url = new URL(String.format(HREF_QR_FORMAT, qrHref, q));
      con = url.openConnection();
      ObjectMapper mapper = getObjectMapper();
      QRDto qrDto = mapper.readValue(con.getInputStream(), QRDto.class);
      app.printQRData(qrDto);
    } catch (Exception e) {
      app.printNoQRData();
    }
    out.println("<br><br>");
    out.println("<div>qr:<a href='https://qr.qxyz.ru/?q=" + q + "'>" + q + "</a></div>");
   } else {
    app.printSearchForm();
   }
 %>
  </div>
  </main>

  <footer>
    <div class="footer-inner">
              <nav>
                <ul class="gap-y-s8">
                  <li><strong><a href='https://qrdemo.qxyz.ru'>QRDEMO</a></strong></li>
                  <li><strong><a href='https://qr.qxyz.ru'>QXYZ</a></strong></li>
                <ul>
            </nav>
          <div>© 2019-2025 Eustrosoft</div>
    </div>
  </footer>
</body>
</html>
