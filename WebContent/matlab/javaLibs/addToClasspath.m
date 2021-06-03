function addToClasspath
    currentDir = fileparts(mfilename('fullpath'));
    libsPath = strcat(currentDir, filesep, 'libs');
    jarSearchPattern = strcat(libsPath, filesep, '*.jar');

    jarsList = dir(jarSearchPattern);

    for i=1:numel(jarsList)
       lib = strcat(libsPath, filesep, jarsList(i).name);
       javaaddpath(lib);
    end

    clear java;
end


