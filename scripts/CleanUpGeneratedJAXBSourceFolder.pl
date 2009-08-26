#!/usr/bin/perl
# important! use only full qualified folder names!
# perl CleanUpGeneratedJAXBSourceFolder.pl /Users/Flori/Documents/workspace34/JavaAPIforKML/src/main/java-gen/de/micromata/opengis
# perl CleanUpGeneratedJAXBSourceFolder.pl /Users/Flori/Documents/workspace34/JaxbPluginAddXmlRootElement/src/main/java-gen/de/micromata/opengis
# perl CleanUpGeneratedJAXBSourceFolder.pl /Users/Flori/Documents/workspace34/JavaAPIforKML/src/main/java-gen/de/micromata/opengis

use warnings;
use strict;
use File::Find;
use File::Basename;
if ( $#ARGV < 0 ) {
	print "arguments needed,\n";
	print "like: \n";
	print "perl CleanUpGeneratedJAXBSource folder\n";
	print "or: \n";
	print "perl CleanUpGeneratedJAXBSource .\n\n";
	exit;
}

my ( $fileCount, $jaxbelementTotalCount, $removeddeprecatedfiles ) = ( 0, 0, 0 );

find( \&removeJAXBElementForOneJavaFile, @ARGV );

print "---------------\n";
print "# of deprecated and removed files: $removeddeprecatedfiles\n";
print "# of eleminated JAXBElements     : $jaxbelementTotalCount\n";
print "# of files processed             : $fileCount\n";
print "---------------\n";

sub removeJAXBElementForOneJavaFile {

	#my $jaxbelementTotalCount) = @_;
	my $filehandle = "";
	$filehandle = $File::Find::name;

	my $jaxbelementLocalCount = 0;
	if ( $filehandle !~ /\.java$/ ) {

		#print "$filehandle isn't a Java-file. Skip.\n";
		return 0;
	}

	# open given file
	open( FH, "+< $filehandle" ) or die "Can't open file: $!\n" && return 0;
	my @currentJavaFile = <FH>;

	# delete all that isn't asked for
	#if ($filehandle =~ /ScaleDeprecated.java/ ) {
	#	$removeddeprecatedfiles++;
	#	unlink($filehandle);
	#	return 0;
	#}
	# delete all that isn't asked for
	#if ($filehandle =~ /SnippetDeprecated.java/ ) {
	#	$removeddeprecatedfiles++;
	#	unlink($filehandle);
	#	return 0;
	#}

	# AltitudeModeEnumType1.java
	#if ($filehandle =~ /AltitudeModeEnumTypeDeprecated.java/ ) {
	#	$removeddeprecatedfiles++;
	#	unlink($filehandle);
	#	return 0;
	#}

	# hihihi (:
	if ( $filehandle =~ /ObjectFactory.java/ ) {
		$removeddeprecatedfiles++;
		unlink($filehandle);
		return 0;
	}

	$fileCount++;

	#iterate over each line in file and replace every JAXBElement occurence
	my $jaxbelementFileCount = 0;
	for ( my $i = 0 ; $i < $#currentJavaFile ; $i++ ) {
		if (   $filehandle !~ /ObjectFactory.java/
			&& $filehandle !~ /ObjectFactoryFlori.java/ )
		{
			if ( $currentJavaFile[$i] =~ s/(.*?<)(JAXBElement<. extends )(.*?)(>)(.*?)/$1$3$5/g ) {

				# print "1: $currentJavaFile[$i]";
				$jaxbelementFileCount++;
			}
			if ( $currentJavaFile[$i] =~ s/(.*?)(JAXBElement<\? extends )(.*?)(>)(.*?)/$1$3$5/g ) {

				# print "2: $currentJavaFile[$i]";
				$jaxbelementFileCount++;
			}
			if (   $currentJavaFile[$i] !~ /(.*?)(JAXBElement<(.*?)> jaxbRootElement = )(.*?)/
				&& $currentJavaFile[$i] =~ s/(.*?)(JAXBElement<)(.*?)(>)(.*?)/$1$3$5/g )
			{

				# print "3: $currentJavaFile[$i]";
				$jaxbelementFileCount++;
			}
			if ( $currentJavaFile[$i] =~ s/(.*?\*.*?)(\{\@link JAXBElement \})(.*?)/$1$3/g ) {

				# print "4: $currentJavaFile[$i]";
				$jaxbelementFileCount++;
			}
			$currentJavaFile[$i] =~ s/(.*?)(\{\@code <\}\{\@link .*?)( \})(\{\@code)( >)(\})(.*?)/$1$2}$4>$6$7/g;
			$currentJavaFile[$i] =~ s/(.*?)(\{\@link .*?)( )(\}.*?)/$1$2$4/g;

			if (   $filehandle !~ /Kml.java/
				&& $currentJavaFile[$i] =~ s/(import javax.xml.bind.JAXBElement;)//g )
			{

				# print "5: $currentJavaFile[$i]";
				$jaxbelementFileCount++;
			}

			if (   $filehandle =~ /Author.java/
				&& $currentJavaFile[$i] =~ /(.*?)(\@XmlElementRefs\(\{.*?$)/ )
			{
				$currentJavaFile[$i]       = "";
				$currentJavaFile[ $i + 1 ] = "";
				$currentJavaFile[ $i + 2 ] = "";
				$currentJavaFile[ $i + 3 ] = "";
				$currentJavaFile[ $i + 4 ] = "";
				$jaxbelementFileCount++;
			}

			if ( $currentJavaFile[$i] =~ s/(.*?)(\@XmlElementRef\(name = .altitudeModeGroup.*?)(, type = .*?.class[, required = false]*.*?)(\))/$1\@XmlElement(defaultValue = \"clampToGround\")/g ) {
				$currentJavaFile[ $i + 1 ] = "    protected AltitudeMode altitudeMode = AltitudeMode.CLAMP_TO_GROUND;\n";
				$jaxbelementFileCount++;
			}

			if ( $currentJavaFile[$i] =~ s/(\@XmlElementRef\(name = .*?)(, type = JAXBElement.class)([, required = false]*)(\))/$1$3$4/g ) {

				# print "4: $currentJavaFile[$i]";
				$jaxbelementFileCount++;
			}

			if ( $currentJavaFile[$i] =~ s/(\@XmlElement\()(type = String.class, )(defaultValue = .*?[, required = false]*)(\))/$1$3$4/g ) {

				# print "4: $currentJavaFile[$i]";
				$jaxbelementFileCount++;
			}

			if ( $currentJavaFile[$i] =~ s/(\@XmlJavaTypeAdapter\(HexBinaryAdapter.class\))//g ) {

				# print "4: $currentJavaFile[$i]";
				$jaxbelementFileCount++;
			}

			if ( $currentJavaFile[$i] =~ s/(this..*? = )(\(\(byte\[\]\))( value)(\))/$1$3/g ) {

				# print "4: $currentJavaFile[$i]";
				$jaxbelementFileCount++;
			}
		}

		if ( $filehandle =~ /LatLonQuad.java/ ) {
			if ( $currentJavaFile[$i] =~ s/(.*?)(\@XmlElement\(namespace = \"http:\/\/www.opengis.net\/kml\/2.2\"\))//g ) {

				# print "5: $currentJavaFile[$i]";
				$jaxbelementFileCount++;
			}

		}

		if (   $filehandle =~ /LatLonQuad.java/
			|| $filehandle =~ /LinearRing.java/
			|| $filehandle =~ /LineString.java/
			|| $filehandle =~ /Point.java/ )
		{

			if ( $currentJavaFile[$i] =~ s/(import javax.xml.bind.annotation.XmlList;)//g ) {

				# print "5: $currentJavaFile[$i]";
				$jaxbelementFileCount++;
			}
			if ( $currentJavaFile[$i] =~ s/(.*?)(\@XmlList)//g ) {

				# print "5: $currentJavaFile[$i]";
				$jaxbelementFileCount++;
			}
		}

		# The AltitudeModeEnum-Case: correct what JAXB hasn't done
		if ( $filehandle =~ /de\/micromata\/opengis\/kml\/v_2_2_0/ ) {
			$currentJavaFile[$i] =~ s/(.*?)(\? )(altitudeMode.*?)/$1 AltitudeMode $3/g;
			$currentJavaFile[$i] =~ s/(.*? public )(\?)( getAltitudeMode.*?)/$1AltitudeMode$3/g;
			$currentJavaFile[$i] =~ s/(.*? public void setAltitudeMode.*?\()(\?)( value.*?)/$1AltitudeMode$3/g;
		} else {
			$currentJavaFile[$i] =~ s/(.*?)(\? )(altitudeModeGroup.*?)/$1 AltitudeModeEnumType $3/g;
			$currentJavaFile[$i] =~ s/(.*? public )(\?)( getAltitudeModeGroup.*?)/$1AltitudeModeEnumType$3/g;
			$currentJavaFile[$i] =~ s/(.*? public void setAltitudeModeGroup.*?\()(\?)( value.*?)/$1AltitudeModeEnumType$3/g;
		}
		$currentJavaFile[$i] =~ s/(.*? this.altitudeMode[Group]* = )(.*?)(value)(\))(;)/$1$3$5/g;

		# @XmlElementRef(name = "AbstractFeatureSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
		# -->
		# @XmlElement(name = "AbstractFeatureSimpleExtensionGroup")
		$currentJavaFile[$i] =~ s/(\@XmlElementRef\()(name = "AbstractFeatureSimpleExtensionGroup")(, namespace = .*?)(\))/\@XmlElement($2)/g;

		if ( $filehandle =~ /Playlist.java/ ) {

			$currentJavaFile[$i] =~ s/import javax.xml.bind.annotation.XmlElementRef;/import javax.xml.bind.annotation.XmlElement;/g;
			$currentJavaFile[$i] =~ s/(\@XmlElementRef\()(name = "AbstractTourPrimitiveGroup")(, namespace = .*?)(\))/\@XmlElement($2)/g;
		}

		#   $currentJavaFile[$i] =~ s/(.*? this.getCoordinates\(\).add\()(coordinates)(\);)/$1new Coordinate($2)$3/g;

		# protected List<?> featureSimpleExtensions;
		# -->
		# @XmlElement(name = "AbstractFeatureSimpleExtensionGroup")
		# @XmlSchemaType(name = "anySimpleType")
		# protected List<Object> featureSimpleExtension;
		if ( $currentJavaFile[$i] =~ /(.*? List<)(\?)(> featureSimpleExtension[s]*;)/ ) {
			$currentJavaFile[$i] =~ s/(.*? List<)(\?)(> featureSimpleExtension[s]*;)/    \@XmlSchemaType(name = "anySimpleType")\n$1Object$3/g;
		}

		# public List<JAXBElement<?>> getFeatureSimpleExtensions() {
		# public List<?> getFeatureSimpleExtension() {
		$currentJavaFile[$i] =~ s/(.*? public List<)(\?)(> getFeatureSimpleExtension[s]*)/$1Object$3/g;

		# featureSimpleExtension = new ArrayList<?>();
		$currentJavaFile[$i] =~ s/(.*? featureSimpleExtension[s]* = new ArrayList<)(\?)(>.*?;)/$1Object$3/g;

		# public Container addToFeatureSimpleExtensions(final JAXBElement<?> featureSimpleExtensions) {
		# public Feature addToFeatureSimpleExtension(final ? featureSimpleExtension) {
		$currentJavaFile[$i] =~ s/(.*? public .*? addToFeatureSimpleExtension[s]*\(final )(\?)( .*?)/$1Object$3/g;

		# public void setFeatureSimpleExtension(final List<?> featureSimpleExtension) {
		$currentJavaFile[$i] =~ s/(.*? public void setFeatureSimpleExtension[s]*\(final List<)(\?)(> .*?)/$1Object$3/g;

		# public Feature withFeatureSimpleExtension(final List<?> featureSimpleExtension) {	 d
		$currentJavaFile[$i] =~ s/(.*? public .*? withFeatureSimpleExtension[s]*\(final List<)(\?)(> .*?)/$1Object$3/g;

		# quick and dirty, do this with the xjc plugin!
		# this.getCoordinates().add(coordinates);
		# -->
		# this.getCoordinates().add(new Coordinates(coordinates));
		$currentJavaFile[$i] =~ s/(.*? this.getCoordinates\(\).add\()(coordinates)(\);)/$1new Coordinate($2)$3/g;

	}

	if ( $jaxbelementFileCount > 0 ) {
		print "$filehandle (JABElements: $jaxbelementFileCount)\n";
	}
	$jaxbelementTotalCount += $jaxbelementFileCount;

	seek( FH, 0, 0 );
	print FH @currentJavaFile;
	truncate( FH, tell(FH) );
	close(FH);
}

